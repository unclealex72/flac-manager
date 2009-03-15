package uk.co.unclealex.music.web.webdav;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class SpringWebdavServlet extends SimpleWebdavServlet {
	
	public static ApplicationContext APPLICATION_CONTEXT;
	
	private SessionProvider i_sessionProvider;
	private DavResourceFactory i_resourceFactory;
	private JcrTemplate i_jcrTemplate;
	private ThreadLocal<Repository> i_currentRepository = new ThreadLocal<Repository>();
	private ThreadLocal<Session> i_currentSession = new ThreadLocal<Session>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		APPLICATION_CONTEXT = applicationContext;
		String jcrTemplateName = getInitParameter("jcr-template");
		if (jcrTemplateName == null) {
			throw new ServletException(
					"You must provide a jcr template parameter for " + config.getServletName());
		}
		JcrTemplate jcrTemplate =
			(JcrTemplate) applicationContext.getBean(jcrTemplateName, JcrTemplate.class);
		if (jcrTemplate == null) {
			throw new ServletException(
					"Cannot find a jcr template called " + jcrTemplateName + " for " + config.getServletName());
		}
		setJcrTemplate(jcrTemplate);
	}
	
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		Thread.currentThread().setName(getServletConfig().getServletName());
		JcrCallback<Object> callback = new JcrCallback<Object>() {
			@Override
			public Object doInJcr(Session session) throws IOException, RepositoryException {
				ThreadLocal<Session> currentSession = getCurrentSession();
				currentSession.set(session);
				ThreadLocal<Repository> currentRepository = getCurrentRepository();
				currentRepository.set(session.getRepository());
				try {
					SpringWebdavServlet.super.service(request, response);
				}
				catch (ServletException e) {
					throw new RepositoryException(e);
				}
				finally {
					currentSession.remove();
					currentRepository.remove();
				}
				return null;
			}
		};
		getJcrTemplate().execute(callback);
	}
	
    /**
     * Returns the <code>DavResourceFactory</code>. If no request factory has
     * been set or created a new instance of {@link ResourceFactoryImpl} is
     * returned.
     *
     * @return the resource factory
     * @see AbstractWebdavServlet#getResourceFactory()
     */
    public DavResourceFactory getResourceFactory() {
        if (i_resourceFactory == null) {
            i_resourceFactory = new EscapingResourceFactoryImpl(getLockManager(), getResourceConfig());
        }
        return i_resourceFactory;
    }

    /**
     * Sets the <code>DavResourceFactory</code>.
     *
     * @param resourceFactory
     * @see AbstractWebdavServlet#setResourceFactory(org.apache.jackrabbit.webdav.DavResourceFactory)
     */
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        i_resourceFactory = resourceFactory;
    }

	@Override
	public synchronized SessionProvider getSessionProvider() {
		if (i_sessionProvider ==null) {
			SessionProvider sessionProvider = new SessionProvider() {
				@Override
				public Session getSession(HttpServletRequest request,
						Repository repository, String workspace)
						throws LoginException, RepositoryException,
						ServletException {
					return getCurrentSession().get();
				}
				@Override
				public void releaseSession(Session session) {
					// Do nothing
				}
			};
			setSessionProvider(sessionProvider);
		}
		return i_sessionProvider;
	}

	@Override
	public synchronized void setSessionProvider(SessionProvider sessionProvider) {
		i_sessionProvider = sessionProvider;
	}
	
	@Override
	public Repository getRepository() {
		return getCurrentRepository().get();
	}

	public JcrTemplate getJcrTemplate() {
		return i_jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {
		i_jcrTemplate = jcrTemplate;
	}

	public ThreadLocal<Repository> getCurrentRepository() {
		return i_currentRepository;
	}

	public ThreadLocal<Session> getCurrentSession() {
		return i_currentSession;
	}	
}
