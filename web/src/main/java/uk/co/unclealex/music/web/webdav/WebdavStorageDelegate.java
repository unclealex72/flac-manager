package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import net.sf.webdav.IWebdavStorage;
import net.sf.webdav.exceptions.WebdavException;

import org.springframework.context.ApplicationContext;

public class WebdavStorageDelegate implements IWebdavStorage {

	private IWebdavStorage i_webdavStorage;
	
	public WebdavStorageDelegate() {
		ApplicationContext applicationContext = SpringWebdavServlet.getApplicationContext();
		IWebdavStorage webdavStorage = (IWebdavStorage) applicationContext.getBean("webdavStorage", IWebdavStorage.class);
		setWebdavStorage(webdavStorage);
	}

	@SuppressWarnings("unchecked")
	public void begin(Principal principal, Hashtable parameters)
			throws WebdavException {
		i_webdavStorage.begin(principal, parameters);
	}

	public void checkAuthentication() throws WebdavException {
		i_webdavStorage.checkAuthentication();
	}

	public void commit() throws WebdavException {
		i_webdavStorage.commit();
	}

	public void createFolder(String folderUri) throws WebdavException {
		i_webdavStorage.createFolder(folderUri);
	}

	public void createResource(String resourceUri) throws WebdavException {
		i_webdavStorage.createResource(resourceUri);
	}

	public String[] getChildrenNames(String folderUri) throws WebdavException {
		return i_webdavStorage.getChildrenNames(folderUri);
	}

	public Date getCreationDate(String uri) throws WebdavException {
		return i_webdavStorage.getCreationDate(uri);
	}

	public Date getLastModified(String uri) throws WebdavException {
		return i_webdavStorage.getLastModified(uri);
	}

	public InputStream getResourceContent(String resourceUri)
			throws WebdavException {
		return i_webdavStorage.getResourceContent(resourceUri);
	}

	public long getResourceLength(String resourceUri) throws WebdavException {
		return i_webdavStorage.getResourceLength(resourceUri);
	}

	public boolean isFolder(String uri) throws WebdavException {
		return i_webdavStorage.isFolder(uri);
	}

	public boolean isResource(String uri) throws WebdavException {
		return i_webdavStorage.isResource(uri);
	}

	public boolean objectExists(String uri) throws WebdavException {
		return i_webdavStorage.objectExists(uri);
	}

	public void removeObject(String uri) throws WebdavException {
		i_webdavStorage.removeObject(uri);
	}

	public void rollback() throws WebdavException {
		i_webdavStorage.rollback();
	}

	public void setResourceContent(String resourceUri, InputStream content,
			String contentType, String characterEncoding) throws WebdavException {
		i_webdavStorage.setResourceContent(resourceUri, content, contentType,
				characterEncoding);
	}

	public IWebdavStorage getWebdavStorage() {
		return i_webdavStorage;
	}

	public void setWebdavStorage(IWebdavStorage webdavStorage) {
		i_webdavStorage = webdavStorage;
	}
	
}
