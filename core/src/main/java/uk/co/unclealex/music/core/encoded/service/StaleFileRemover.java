package uk.co.unclealex.music.core.encoded.service;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;

public class StaleFileRemover {

	private Collection<String> i_filenames;
	private File i_rootDirectory;
	private String i_extension;
	private FileFilter i_filter;
	
	public StaleFileRemover(Collection<String> filenames, File rootDirectory,
			String extension) {
		super();
		i_filenames = filenames;
		i_rootDirectory = rootDirectory;
		i_extension = extension;
		i_filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || getExtension().equals(FilenameUtils.getExtension(pathname.getPath()));
			}
		};
	}

	public void removeAll() {
		removeAll(getRootDirectory());
	}
	
	public void removeAll(File directory) {
		for (File f : directory.listFiles(getFilter())) {
			if (f.isDirectory()) {
				removeAll(f);
			}
			else {
				String relativePath = f.getPath().substring(getRootDirectory().getPath().length());
				if (relativePath.startsWith(File.separator)) {
					relativePath = relativePath.substring(File.separator.length());
				}
				if (!getFilenames().contains(relativePath)) {
					f.delete();
				}
			}
		}
		if (!directory.equals(getRootDirectory()) && directory.list().length == 0) {
			directory.delete();
		}
	}
	
	public Collection<String> getFilenames() {
		return i_filenames;
	}
	public void setFilenames(Collection<String> filenames) {
		i_filenames = filenames;
	}
	public File getRootDirectory() {
		return i_rootDirectory;
	}
	public void setRootDirectory(File rootDirectory) {
		i_rootDirectory = rootDirectory;
	}
	public String getExtension() {
		return i_extension;
	}
	public void setExtension(String extension) {
		i_extension = extension;
	}

	public FileFilter getFilter() {
		return i_filter;
	}

	public void setFilter(FileFilter filter) {
		i_filter = filter;
	}
	
}
