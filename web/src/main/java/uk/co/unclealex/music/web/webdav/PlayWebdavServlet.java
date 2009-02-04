package uk.co.unclealex.music.web.webdav;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.SessionProviderImpl;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;

public class PlayWebdavServlet extends SimpleWebdavServlet {

	private Repository i_repository;
	private SessionProvider i_sessionProvider;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		try {
			String home = System.getProperty("java.io.tmpdir") + "/play";
			RepositoryConfig config =
				RepositoryConfig.create(
					TransientRepository.class.getResource("repository.xml").toURI(), home);
			Repository repository = new TransientRepository(config);
			setRepository(repository);
			Session session = repository.login(new SimpleCredentials("alex", "password".toCharArray()));
			showRepository(session.getRootNode());
		} 
		catch (ConfigurationException e) {
			throw new ServletException(e);
		} 
		catch (URISyntaxException e) {
			throw new ServletException(e);
		} 
		catch (IOException e) {
			throw new ServletException(e);
		}
		catch (RepositoryException e) {
			throw new ServletException(e);
		}
		super.init(servletConfig);
	}

	protected void showRepository(Node node) throws RepositoryException {
		System.out.println("Node " + node.getPath());
		for (PropertyIterator iter = node.getProperties(); iter.hasNext(); ) {
			Property property = iter.nextProperty();
			Value[] values;
			try {
				values = property.getValues();
			}
			catch (ValueFormatException e) {
				values = new Value[] { property.getValue() };
			}
			for (Value value : values) {
				int type = property.getType();
				System.out.print("  " + property.getName() + " = (" + PropertyType.nameFromValue(type) + ") ");
				String str;
				switch (type) {
				case PropertyType.BINARY: {
					str = "binary";
					break;
				}
				case PropertyType.BOOLEAN: {
					str = Boolean.toString(value.getBoolean());
					break;
				}
				case PropertyType.DATE: {
					str = value.getDate().getTime().toString();
					break;
				}
				case PropertyType.DOUBLE: {
					str = Double.toString(value.getDouble());
					break;
				}
				case PropertyType.LONG: {
					str = Long.toString(value.getLong());
					break;
				}
				case PropertyType.UNDEFINED: {
					str = "undefined";
					break;
				}
				default : {
					str = value.getString();
					break;
				}
				}
				System.out.println(str);
			}
		}
		for (NodeIterator iter = node.getNodes(); iter.hasNext(); ) {
			showRepository(iter.nextNode());
		}
	}
	@Override
	public synchronized SessionProvider getSessionProvider() {
		if (i_sessionProvider ==null) {
			SessionProvider sessionProvider = new SessionProviderImpl(getCredentialsProvider()) {
				@Override
				public Session getSession(HttpServletRequest request,
						Repository repository, String workspace)
						throws LoginException, RepositoryException,
						ServletException {
					return super.getSession(request, repository, null);
				}
			};
			setSessionProvider(sessionProvider);
		}
		return i_sessionProvider;
	}

	@Override
	public Repository getRepository() {
		return i_repository;
	}

	public void setRepository(Repository repository) {
		i_repository = repository;
	}

	public void setSessionProvider(SessionProvider sessionProvider) {
		i_sessionProvider = sessionProvider;
	}
	

}
