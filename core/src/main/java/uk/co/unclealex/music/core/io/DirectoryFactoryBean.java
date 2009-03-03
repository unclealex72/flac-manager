package uk.co.unclealex.music.core.io;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

public class DirectoryFactoryBean implements FactoryBean {

	private static final Logger log = Logger.getLogger(DirectoryFactoryBean.class);
	
	private String i_path;
	private File i_directory;
	private boolean i_temporary;
	
	@PostConstruct
	public void initialise() throws IOException {
		File directory;
		if (!isTemporary()) {
			directory = new File(getPath()).getCanonicalFile();
		}
		else {
			directory = new File(new File(System.getProperty("java.io.tmpdir")), getPath());
		}
		if (directory.exists() && (!directory.canWrite() || !directory.isDirectory())) {
			throw new IllegalArgumentException("Cannot create directory " + directory + " as it already exists and is not a writable directory.");
		}
		setDirectory(directory);
		removeIfTemporary();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IllegalArgumentException("Could not create directory " + directory);
		}
	}
	
	@PreDestroy
	public void destroy() {
		try {
			removeIfTemporary();
		}
		catch (IOException e) {
			log.warn("Could not remove temporary directory " + getDirectory(), e);
		}
	}
	
	protected void removeIfTemporary() throws IOException {
		if (isTemporary()) {
			File directory = getDirectory();
			log.info("Removing directory " + directory + " as it is temporary.");
			FileUtils.deleteDirectory(directory);
		}
	}

	@Override
	public Object getObject() {
		return getDirectory();
	}

	@Override
	public Class<File> getObjectType() {
		return File.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getPath() {
		return i_path;
	}

	@Required
	public void setPath(String path) {
		i_path = path;
	}

	public File getDirectory() {
		return i_directory;
	}

	public void setDirectory(File directory) {
		i_directory = directory;
	}

	public boolean isTemporary() {
		return i_temporary;
	}

	public void setTemporary(boolean temporary) {
		i_temporary = temporary;
	}

}
