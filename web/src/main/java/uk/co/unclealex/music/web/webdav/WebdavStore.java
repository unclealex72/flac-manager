package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;
import java.util.SortedSet;

import org.apache.commons.transaction.util.LoggerFacade;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.simple.store.BasicWebdavStore;
import org.apache.slide.structure.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.service.TrackStreamService;
import uk.co.unclealex.music.core.service.filesystem.FileSystemService;
import uk.co.unclealex.music.core.service.filesystem.PathNotFoundException;

@Component
public class WebdavStore implements BasicWebdavStore {

	private FileSystemService i_fileSystemService;
	private TrackStreamService i_trackStreamService;
	
	private int i_scopeLength;
	
	@SuppressWarnings("unchecked")
	@Override
	public void begin(Service service, Principal principal, Object obj,
			LoggerFacade loggerFacade, Hashtable properties) throws ServiceAccessException,
			ServiceParameterErrorException, ServiceParameterMissingException {
		if (getFileSystemService() == null) {
			ApplicationContext applicationContext = SpringWebdavServlet.getApplicationContext();
			applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
				this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
			WebdavStoreAdapter adapter = (WebdavStoreAdapter) service;
			setScopeLength(adapter.getScopeLength());
		}
	}

	public void checkAuthentication() {
	}

	public void commit() {
	}

	public void createFolder(String folderUri) throws AccessDeniedException {
		readOnly(folderUri);
	}

	public void createResource(String resourceUri) throws AccessDeniedException {
		readOnly(resourceUri);
	}

	public String[] getChildrenNames(String folderUri) throws ObjectNotFoundException {
		SortedSet<String> childrenNames = new Callback<SortedSet<String>>() {
			@Override
			public SortedSet<String> doInFileSystemService(
					FileSystemService fileSystemService, String uri) throws PathNotFoundException {
				return fileSystemService.getChildren(uri);
			}
		}.execute(folderUri);
		return childrenNames==null?null:childrenNames.toArray(new String[0]);
	}

	public Date getCreationDate(String uri) throws ObjectNotFoundException {
		return new Callback<Date>() {
			@Override
			public Date doInFileSystemService(FileSystemService fileSystemService, String uri) throws PathNotFoundException {
				return fileSystemService.getModificationDate(uri);
			}
		}.execute(uri);
	}

	public Date getLastModified(String uri) throws ObjectNotFoundException {
		return getCreationDate(uri);
	}

	protected EncodedTrackBean getTrackForPath(String resourceUri) throws ObjectNotFoundException {
		return new Callback<EncodedTrackBean>() {
			@Override
			public EncodedTrackBean doInFileSystemService(FileSystemService fileSystemService, String uri)
					throws PathNotFoundException {
				return fileSystemService.findByPath(uri);
			}
		}.execute(resourceUri);
	}
	
	public InputStream getResourceContent(String resourceUri) throws ObjectNotFoundException {
		return getTrackStreamService().getTrackInputStream(getTrackForPath(resourceUri));
	}

	public long getResourceLength(String resourceUri) throws ObjectNotFoundException {
		return new Callback<Long>() {
			@Override
			public Long doInFileSystemService(FileSystemService fileSystemService, String uri) throws PathNotFoundException {
				return fileSystemService.getLength(uri);
			}
		}.execute(resourceUri);
	}

	public boolean isFolder(String uri) {
		try {
			return new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService, String uri) throws PathNotFoundException {
					return fileSystemService.isDirectory(uri);
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			return false;
		}
	}

	public boolean isResource(String uri) {
		try {
			return new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService,
						String uri) throws PathNotFoundException {
					return fileSystemService.findByPath(uri) != null;
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			return false;
		}
	}

	public boolean objectExists(String uri) {
		try {
			return new Callback<Boolean>() {
				@Override
				public Boolean doInFileSystemService(FileSystemService fileSystemService,
						String uri) throws PathNotFoundException {
					return fileSystemService.objectExists(uri);
				}
			}.execute(uri);
		}
		catch (ObjectNotFoundException e) {
			return false;
		}
	}

	public void removeObject(String uri) throws AccessDeniedException {
		readOnly(uri);
	}

	public void rollback() {
	}

	public void setResourceContent(String resourceUri, InputStream content,
			String contentType, String characterEncoding) throws AccessDeniedException {
		readOnly(resourceUri);
	}

	protected void readOnly(String folderUri) throws AccessDeniedException {
		//throw new AccessDeniedException(folderUri, folderUri, folderUri);
	}

	protected abstract class Callback<E> {
		
		public abstract E doInFileSystemService(FileSystemService fileSystemService, String uri) throws PathNotFoundException;
		public E execute(String uri) throws ObjectNotFoundException {
			try {
				return doInFileSystemService(getFileSystemService(), uri.substring(getScopeLength()));
			}
			catch (PathNotFoundException e) {
				throw new ObjectNotFoundException(uri);
			}
		}
	}
	
	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	@Required
	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

	public TrackStreamService getTrackStreamService() {
		return i_trackStreamService;
	}

	@Required
	public void setTrackStreamService(TrackStreamService trackStreamService) {
		i_trackStreamService = trackStreamService;
	}

	public int getScopeLength() {
		return i_scopeLength;
	}

	public void setScopeLength(int contextLength) {
		i_scopeLength = contextLength;
	}
}
