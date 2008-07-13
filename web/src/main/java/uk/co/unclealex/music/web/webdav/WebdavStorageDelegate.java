package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.UnauthenticatedException;
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNotFoundException;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.webdav.SpringWebdavServlet;

public class WebdavStorageDelegate implements BasicWebdavStore {

	private BasicWebdavStore i_webdavStore;
	
	public WebdavStorageDelegate() {
		ApplicationContext applicationContext = SpringWebdavServlet.getApplicationContext();
		WebdavStore webdavStorage = (WebdavStore) applicationContext.getBean("webdavStore", WebdavStore.class);
		setWebdavStore(webdavStorage);
	}

	public void checkAuthentication() throws UnauthenticatedException {
		i_webdavStore.checkAuthentication();
	}

	
	@SuppressWarnings("unchecked")
	public void begin(Service arg0, Principal arg1, Object arg2,
			LoggerFacade arg3, Hashtable arg4) throws ServiceAccessException,
			ServiceParameterErrorException, ServiceParameterMissingException {
		i_webdavStore.begin(arg0, arg1, arg2, arg3, arg4);
	}

	public void commit() throws ServiceAccessException {
		i_webdavStore.commit();
	}

	public void createFolder(String folderUri) throws AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException, ServiceAccessException {
		i_webdavStore.createFolder(folderUri);
	}

	public void createResource(String resourceUri) throws AccessDeniedException, ObjectAlreadyExistsException, ObjectLockedException, ServiceAccessException {
		i_webdavStore.createResource(resourceUri);
	}

	public String[] getChildrenNames(String folderUri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.getChildrenNames(folderUri);
	}

	public Date getCreationDate(String uri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.getCreationDate(uri);
	}

	public Date getLastModified(String uri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.getLastModified(uri);
	}

	public InputStream getResourceContent(String resourceUri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.getResourceContent(resourceUri);
	}

	public long getResourceLength(String resourceUri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.getResourceLength(resourceUri);
	}

	public boolean isFolder(String uri) throws AccessDeniedException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.isFolder(uri);
	}

	public boolean isResource(String uri) throws AccessDeniedException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.isResource(uri);
	}

	public boolean objectExists(String uri) throws AccessDeniedException, ObjectLockedException, ServiceAccessException {
		return i_webdavStore.objectExists(uri);
	}

	public void removeObject(String uri) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		i_webdavStore.removeObject(uri);
	}

	public void rollback() throws ServiceAccessException {
		i_webdavStore.rollback();
	}

	public void setResourceContent(String resourceUri, InputStream content,
			String contentType, String characterEncoding) throws AccessDeniedException, ObjectNotFoundException, ObjectLockedException, ServiceAccessException {
		i_webdavStore.setResourceContent(resourceUri, content, contentType,
				characterEncoding);
	}

	public BasicWebdavStore getWebdavStore() {
		return i_webdavStore;
	}

	public void setWebdavStore(BasicWebdavStore webdavStore) {
		i_webdavStore = webdavStore;
	}
	
}
