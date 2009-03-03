package uk.co.unclealex.music.web.webdav;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

public class SpringWebdavServlet extends SimpleWebdavServlet {
	
	public static ApplicationContext APPLICATION_CONTEXT;
	
	private RepositoryManager i_repositoryManager;
	private SessionProvider i_sessionProvider;
	private DavResourceFactory i_resourceFactory;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		APPLICATION_CONTEXT = applicationContext;
		String repositoryManagerName = getInitParameter("repository-manager");
		if (repositoryManagerName == null) {
			throw new ServletException(
					"You must provide a repository manager parameter for " + config.getServletName());
		}
		RepositoryManager repositoryManager =
			(RepositoryManager) applicationContext.getBean(repositoryManagerName, RepositoryManager.class);
		if (repositoryManager == null) {
			throw new ServletException(
					"Cannot find a repository manager called " + repositoryManagerName + " for " + config.getServletName());
		}
		setRepositoryManager(repositoryManager);
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
					return getRepositoryManager().getSession();
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
		return getRepositoryManager().getRepository();
	}
	
	public RepositoryManager getRepositoryManager() {
		return i_repositoryManager;
	}
	
	public void setRepositoryManager(RepositoryManager repositoryManager) {
		i_repositoryManager = repositoryManager;
	}
	
}
