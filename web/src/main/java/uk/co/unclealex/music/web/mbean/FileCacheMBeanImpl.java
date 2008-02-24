package uk.co.unclealex.music.web.mbean;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import uk.co.unclealex.music.core.service.filesystem.FileSystemService;

@ManagedResource(
	objectName="bean:name=uk.co.unclealex.music.fileCache", description="WebDav file cache MBean")
@Component
public class FileCacheMBeanImpl implements FileCacheMBean {

	private static final Logger log = Logger.getLogger(FileCacheMBeanImpl.class);
	
	private FileSystemService i_fileSystemService;
	
	@PostConstruct
	public void initialise() {
		log.info("The WebDav file cache MBean has been loaded.");
	}
	
	@Override
	@ManagedOperation(description="Refresh the file cache")
	public void refreshCache() {
		log.info("Rebuilding cache via mbean call");
		getFileSystemService().rebuildCache();
	}

	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

}
