package uk.co.unclealex.music.fs;

import java.io.IOException;
import java.util.Set;

import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;

public interface FileSystemProvider {

	/**
	 * Find a file by its path. A path is expected to not start with a slash.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public FileBean findByPath(String path) throws IOException;

	public long getTotalSize();

	public int countFiles();

	public int countChildren(DirectoryFileBean directoryFileBean);

	/**
	 * Explicitly get the children of a directory, and thus remove the need to lazy load. 
	 * @param directoryFileBean
	 * @return
	 */
	public Set<FileBean> getChildren(DirectoryFileBean directoryFileBean);

}
