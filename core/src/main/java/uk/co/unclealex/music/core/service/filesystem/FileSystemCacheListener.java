package uk.co.unclealex.music.core.service.filesystem;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.FileSystemCacheDao;

@Transactional
public class FileSystemCacheListener extends Thread {

	private static final Log log = LogFactory.getLog(FileSystemCacheListener.class);
	
	private FileSystemService i_fileSystemService;
	private FileSystemCacheDao i_fileSystemCacheDao;
	private int i_waitInSeconds;
	
	@Override
	public void run() {
		FileSystemService fileSystemService = getFileSystemService();
		FileSystemCacheDao fileSystemCacheDao = getFileSystemCacheDao();
		
		fileSystemService.rebuildCache();
		long wait = getWaitInSeconds() * 1000;
		try {
			while (!isInterrupted()) {
				if (fileSystemCacheDao.isRebuildRequired()) {
					log.info("Rebuilding the file system cache.");
					fileSystemService.rebuildCache();
					fileSystemCacheDao.setRebuildRequired(false);
				}
				if (!isInterrupted()) {
					sleep(wait);
				}
			}
		}
		catch (InterruptedException e) {
			// Do nothing
		}
	}
	
	@PostConstruct
	public void initialise() {
		start();
	}
	
	@PreDestroy
	public void remove() {
		interrupt();
	}

	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	@Required
	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

	public FileSystemCacheDao getFileSystemCacheDao() {
		return i_fileSystemCacheDao;
	}

	@Required
	public void setFileSystemCacheDao(FileSystemCacheDao filesystemCacheDao) {
		i_fileSystemCacheDao = filesystemCacheDao;
	}

	public int getWaitInSeconds() {
		return i_waitInSeconds;
	}

	@Required
	public void setWaitInSeconds(int waitInSeconds) {
		i_waitInSeconds = waitInSeconds;
	}

}
