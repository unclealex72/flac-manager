package uk.co.unclealex.music.web.encoded.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.log4j.Logger;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.simple.authentication.SessionAuthenticationManager;
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.structure.ObjectNotFoundException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.web.encoded.model.EncodedTrackBean;
import uk.co.unclealex.music.web.encoded.service.FileSystemService;
import uk.co.unclealex.music.web.encoded.service.SingleEncoderService;

public class WebdavStore implements BasicWebdavStore, SessionAuthenticationManager {

	private static final Logger log = Logger.getLogger(WebdavStore.class);
	
	public WebdavStore() {
		ApplicationContext applicationContext = SpringWebdavServlet.getApplicationContext();
		AutowireCapableBeanFactory factory = applicationContext .getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}
	
	private FileSystemService i_fileSystemService;
	private SingleEncoderService i_singleEncoderService;
	
	private Service i_service;
	private Principal i_principal;
	private Object i_connection;
	private LoggerFacade i_logger;
	private Hashtable<?, ?> i_paramters;
	
	protected EncodedTrackBean getEncodedTrackBean(String uri, FileSystemService fileSystemService) throws ObjectNotFoundException {
		EncodedTrackBean encodedTrackBean = fileSystemService.getEncodedTrackBean(uri);
		if (encodedTrackBean == null) {
			throw new ObjectNotFoundException(uri);
		}
		return encodedTrackBean;
	}
	
  @SuppressWarnings("unchecked")
	public void begin(
		Service service, Principal principal, Object connection, LoggerFacade logger, Hashtable parameters) {
  	setService(service);
  	setPrincipal(principal);
  	setConnection(connection);
  	setLogger(logger);
  	setParamters(parameters);
  }

	@Override
	public void checkAuthentication() {
		// No authentication yet.
	}

	@Override
	public void commit() {
  	// Read only, no need to do anything.
	}

	protected void readOnly(String uri) throws AccessDeniedException {
		log.warn("Read attempt to " + uri);
		//throw new AccessDeniedException("This filesystem is read only.", uri, uri);
	}
	
	@Override
	public void createFolder(String folderUri) throws ServiceAccessException,
			AccessDeniedException {
		readOnly(folderUri);
	}

	@Override
	public void createResource(String resourceUri) throws AccessDeniedException {
		readOnly(resourceUri);
	}

	@Override
	public String[] getChildrenNames(String folderUri)
			throws ServiceAccessException, AccessDeniedException,
			ObjectNotFoundException, ObjectLockedException {
		Callback<String[]> callback = new Callback<String[]>() {
			@Override
			public String[] doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) {
				return fileSystemService.getChildPaths(modifiedUri);
			}
		};
		String[] names = callback.execute(folderUri);
		if (names == null) {
			throw new ObjectNotFoundException(folderUri);
		}
		return names;
	}

	@Override
	public Date getCreationDate(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		return new Callback<Date>() {
			@Override
			public Date doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) throws ObjectNotFoundException {
				return fileSystemService.getCreationDate(modifiedUri);
			}
		}.execute(uri);
	}

	@Override
	public Date getLastModified(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		return new Callback<Date>() {
			@Override
			public Date doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) throws ObjectNotFoundException {
				return fileSystemService.getCreationDate(modifiedUri);
			}
		}.execute(uri);
	}

	@Override
	public InputStream getResourceContent(String resourceUri)
			throws ServiceAccessException, AccessDeniedException,
			ObjectNotFoundException, ObjectLockedException {
		return new Callback<InputStream>() {
			@Override
			public InputStream doInFileSystemService(
					FileSystemService fileSystemService, String modifiedUri) throws ObjectNotFoundException {
				return fileSystemService.getResourceAsStream(modifiedUri);
			}
		}.execute(resourceUri);
	}

	@Override
	public long getResourceLength(String resourceUri)
			throws ServiceAccessException, AccessDeniedException,
			ObjectNotFoundException, ObjectLockedException {
		return  new Callback<Long>() {
			@Override
			public Long doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) throws ObjectNotFoundException {
				return fileSystemService.getLength(modifiedUri);
			}
		}.execute(resourceUri);
	}

	@Override
	public boolean isFolder(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectLockedException {
		boolean isFolder;
		try {
			isFolder = new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) {
					return fileSystemService.isDirectory(modifiedUri);
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			isFolder = false;
		}
		return isFolder;
	}

	@Override
	public boolean isResource(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectLockedException {
		try {
			return new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService,
						String modifiedUri) {
					return fileSystemService.isResource(modifiedUri);
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean objectExists(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectLockedException {
		boolean objectExists;
		try {
			objectExists = new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) {
					return fileSystemService.exists(modifiedUri);
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			objectExists = false;
		}
			return objectExists;
	}

	@Override
	public void removeObject(String uri) throws ServiceAccessException,
			AccessDeniedException, ObjectNotFoundException, ObjectLockedException {
		readOnly(uri);
	}

	@Override
	public void rollback() throws ServiceAccessException {
		// Read only filesystem, nothing to do.
	}

	@Override
	public void setResourceContent(String resourceUri, InputStream content,
			String contentType, String characterEncoding)
			throws ServiceAccessException, AccessDeniedException,
			ObjectNotFoundException, ObjectLockedException {
		readOnly(resourceUri);
	}

	@Override
	public void closeAuthenticationSession(Object session) {
		// Nothing to do.
	}
	
	@Override
	public Object getAuthenticationSession(String username) {
		return username;
	}
	
	@Override
	public Object getAuthenticationSession(String username, String password) {
		return username;
	}
	
	protected abstract class Callback<E> {
		public E execute(String uri) throws ServiceAccessException, ObjectNotFoundException {
			try {
				String modifiedUri = "";
				int rootPathLength = "/files".length();
				if (uri.length() >  rootPathLength) {
					modifiedUri = uri.substring(rootPathLength);
				}
				return doInFileSystemService(getFileSystemService(), modifiedUri);
			}
			catch (ObjectNotFoundException e) {
				throw e;
			}
			catch (Throwable t) {
				throw new ServiceAccessException(getService(), t);
			}
		}

		public abstract E doInFileSystemService(FileSystemService fileSystemService, String modifiedUri) throws Exception;
	}
	
	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

	public Service getService() {
		return i_service;
	}

	public void setService(Service service) {
		i_service = service;
	}

	public Principal getPrincipal() {
		return i_principal;
	}

	public void setPrincipal(Principal principal) {
		i_principal = principal;
	}

	public Object getConnection() {
		return i_connection;
	}

	public void setConnection(Object connection) {
		i_connection = connection;
	}

	public LoggerFacade getLogger() {
		return i_logger;
	}

	public void setLogger(LoggerFacade logger) {
		i_logger = logger;
	}

	public Hashtable<?, ?> getParamters() {
		return i_paramters;
	}

	public void setParamters(Hashtable<?, ?> paramters) {
		i_paramters = paramters;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}
}
