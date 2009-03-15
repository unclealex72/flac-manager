package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.log4j.Logger;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;

import uk.co.unclealex.music.base.service.filesystem.ClearableRepositoryFactory;

public class ClearableRepositoryFactoryImpl implements ClearableRepositoryFactory {

	private static final Logger log = Logger.getLogger(ClearableRepositoryFactoryImpl.class);
	
	private RepositoryConfig i_repositoryConfig;
	private Resource i_configuration;
	private Resource i_homeDir;
	private boolean i_clearRequested;
	
	@PostConstruct
	public void initialise() throws ConfigurationException, IOException {
		RepositoryConfig repositoryConfig =
			RepositoryConfig.create(new InputSource(getConfiguration().getInputStream()),
				getHomeDir().getFile().getAbsolutePath());
		setRepositoryConfig(repositoryConfig);
	}
	
	@Override
	public void clearNextInstance() {
		setClearRequested(true);
	}

	@Override
	public RepositoryImpl getRepository() throws RepositoryException {
		try {
			try {
				if (isClearRequested()) {
					clearRepository();
				}
				return create(getRepositoryConfig());
			}
			catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
		finally {
			setClearRequested(false);
		}
	}

	protected RepositoryImpl create(RepositoryConfig repositoryConfig) throws RepositoryException {
		return RepositoryImpl.create(repositoryConfig);
	}
	
	@SuppressWarnings("unchecked")
	protected void clearRepository() throws IOException {
		File homeDir = getHomeDir().getFile();
		if (homeDir.exists()) {
			removeIndexDirectories(homeDir);
			Map<File, String> workspaceConfigurations = new HashMap<File, String>();
			Collection<File> files =
				FileUtils.listFiles(homeDir, FileFilterUtils.nameFileFilter("workspace.xml"), FileFilterUtils.trueFileFilter());
			for (File file : files) {
				StringWriter writer = new StringWriter();
				FileReader reader = new FileReader(file);
				IOUtils.copy(reader, writer);
				workspaceConfigurations.put(file, writer.toString());
				IOUtils.closeQuietly(reader);				
			}
			log.info("Removing directory " + homeDir);
			FileUtils.deleteDirectory(homeDir);
			if (workspaceConfigurations.isEmpty()) {
				homeDir.mkdirs();
			}
			else {
				for (Map.Entry<File, String> entry : workspaceConfigurations.entrySet()) {
					File workspaceFile = entry.getKey();
					String configuration = entry.getValue();
					workspaceFile.getParentFile().mkdirs();
					FileWriter writer = new FileWriter(workspaceFile);
					writer.write(configuration);
					IOUtils.closeQuietly(writer);
				}
			}
		}
	}

	protected void removeIndexDirectories(File dir) throws IOException {
		if (dir.getParentFile().getName().equals("index")) {
			FSDirectory directory = FSDirectory.getDirectory(dir, null);
			directory.close();
		}
		else {
			FileFilter directoryFilter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
			File[] childDirectories = dir.listFiles(directoryFilter);
			for (File child : childDirectories) {
				removeIndexDirectories(child);
			}
		}
	}

	public RepositoryConfig getRepositoryConfig() {
		return i_repositoryConfig;
	}

	public void setRepositoryConfig(RepositoryConfig repositoryConfig) {
		i_repositoryConfig = repositoryConfig;
	}

	public Resource getConfiguration() {
		return i_configuration;
	}

	@Required
	public void setConfiguration(Resource configuration) {
		i_configuration = configuration;
	}
	
	public Resource getHomeDir() {
		return i_homeDir;
	}
	
	@Required
	public void setHomeDir(Resource homeDir) {
		i_homeDir = homeDir;
	}
	public boolean isClearRequested() {
		return i_clearRequested;
	}
	public void setClearRequested(boolean clearRequested) {
		i_clearRequested = clearRequested;
	}

}
