package uk.co.unclealex.music.sync;

import java.io.File;

public class LocalFile extends RelativePathFile<LocalFile> {

	private File i_file;
	
	public LocalFile(File file, String relativePath) {
		super(relativePath);
		i_file = file;
	}

	@Override
	public long getActualLastModified() {
		return getFile().lastModified();
	}
	
	public File getFile() {
		return i_file;
	}

}
