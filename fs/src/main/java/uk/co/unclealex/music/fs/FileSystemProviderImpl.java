package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;

public class FileSystemProviderImpl implements FileSystemProvider {

	private FileDao i_fileDao;
	
	@Override
	public int countChildren(DirectoryFileBean directoryFileBean) {
		return getFileDao().countChildren(directoryFileBean);
	}

	@Override
	public int countFiles() {
		return (int) getFileDao().count();
	}

	@Override
	public Set<FileBean> getChildren(DirectoryFileBean directoryFileBean) {
		return getFileDao().getChildren(directoryFileBean);
	}
	
	@Override
	public FileBean findByPath(String path) throws IOException {
		return getFileDao().findByPath(path);
	}

	@Override
	public long getTotalSize() {
		long totalSize = 0;
		for (String path : getFileDao().findAllRealPaths()) {
			totalSize += new File(path).length();
		}
		return totalSize;
	}

	public FileDao getFileDao() {
		return i_fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		i_fileDao = fileDao;
	}

}
