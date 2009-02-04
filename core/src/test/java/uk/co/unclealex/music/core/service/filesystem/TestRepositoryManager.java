package uk.co.unclealex.music.core.service.filesystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestRepositoryManager<E> extends RepositoryManagerImpl<E> {

	private static final Logger log = Logger.getLogger(TestRepositoryManager.class);
	@PostConstruct
	public void initialise() throws IOException, URISyntaxException, RepositoryException {
		setBaseDirectory(System.getProperty("java.io.tmpdir"));
		File repositoryDirectory = getRepositoryDirectory();
		if (repositoryDirectory.exists()) {
			FileUtils.deleteDirectory(repositoryDirectory);
		}
		super.initialise();
	}
	
	@PreDestroy
	public void destroy() {
		super.destroy();
		File repositoryDirectory = getRepositoryDirectory();
		try {
			FileUtils.deleteDirectory(repositoryDirectory);
		}
		catch (IOException e) {
			log.warn("Could not delete directory " + repositoryDirectory, e);
		}
	}
}