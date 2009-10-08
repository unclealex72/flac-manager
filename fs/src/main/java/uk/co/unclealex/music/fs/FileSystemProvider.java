package uk.co.unclealex.music.fs;

import java.io.IOException;

import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.FileBean;

public interface FileSystemProvider {

	public FileBean findByPath(String path) throws IOException;

	public int countChildren(DirectoryFileBean directoryFileBean);

	public long getTotalSize();

	public int countFiles();

}
