package uk.co.unclealex.music.web.encoded.webdav;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import net.sf.webdav.IWebdavStorage;
import net.sf.webdav.exceptions.WebdavException;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.core.service.filesystem.FileSystemService;
import uk.co.unclealex.music.web.webdav.SpringWebdavServlet;

public class WebdavStore implements IWebdavStorage {

	//private static final Logger log = Logger.getLogger(WebdavStore.class);
	
	public WebdavStore() {
		ApplicationContext applicationContext = SpringWebdavServlet.getApplicationContext();
		AutowireCapableBeanFactory factory = applicationContext .getAutowireCapableBeanFactory();
		factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}
	
	private FileSystemService i_fileSystemService;

	@SuppressWarnings("unchecked")
	@Override
	public void begin(Principal principal, Hashtable parameters)
			throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void checkAuthentication() throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void commit() throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void createFolder(String folderUri) throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void createResource(String resourceUri) throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public String[] getChildrenNames(String folderUri) throws WebdavException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getCreationDate(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getLastModified(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getResourceContent(String resourceUri)
			throws WebdavException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getResourceLength(String resourceUri) throws WebdavException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isFolder(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isResource(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean objectExists(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeObject(String uri) throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void rollback() throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public void setResourceContent(String resourceUri, InputStream content,
			String contentType, String characterEncoding) throws WebdavException {
		// TODO Auto-generated method stub
		
	}

	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

}
