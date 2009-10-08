package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.commons.io.FilenameUtils;

import uk.co.unclealex.music.base.model.DataFileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.visitor.FileVisitor;
import fuse.Errno;
import fuse.FuseDirFiller;
import fuse.FuseException;
import fuse.FuseFtypeConstants;
import fuse.FuseGetattrSetter;
import fuse.FuseOpenSetter;
import fuse.FuseStatfsSetter;

public class FileSystem extends ReadOnlyFileSystem {

	private static final int BLOCK_SIZE = 512;
  private static final int NAME_LENGTH = 1024;

	private FileSystemProvider i_fileSystemProvider;
	
	@Override
	public int flush(String path, Object fh) throws FuseException {
		return 0;
	}

	@Override
	public int fsync(String path, Object fh, boolean isDatasync) throws FuseException {
		return 0;
	}

	@Override
	public int getattr(String path, final FuseGetattrSetter getattrSetter) throws FuseException {
    final int time = (int) (System.currentTimeMillis() / 1000L);
		FileVisitor<Integer, Exception> fileVisitor = new FileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				int childCount = getFileSystemProvider().countChildren(directoryFileBean);
        getattrSetter.set(
            directoryFileBean.getId(),
            getMode(directoryFileBean),
            1,
            0, 0,
            0,
            childCount * NAME_LENGTH,
            (childCount * NAME_LENGTH + BLOCK_SIZE - 1) / BLOCK_SIZE,
            time, time, time);
        return 0;
			}
			@Override
			public Integer visit(DataFileBean dataFileBean) {
				long fileSize = dataFileBean.getFile().length();
        getattrSetter.set(
            dataFileBean.getId(),
            getMode(dataFileBean),
            1,
            0, 0,
            0,
            fileSize,
            (fileSize + BLOCK_SIZE - 1) / BLOCK_SIZE,
            time, time, time);
        return 0;
			}
		};
		return visitPath(path, fileVisitor);
	}

	@Override
	public int getdir(String path, final FuseDirFiller dirFiller) throws FuseException {
		FileVisitor<Integer, Exception> fileVisitor = new FileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DataFileBean dataFileBean) {
				return Errno.EBADF;
			}
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				for (FileBean fileBean : directoryFileBean.getChildren()) {
					dirFiller.add(FilenameUtils.getName(fileBean.getPath()), fileBean.getId(), getMode(fileBean));
				}
				return 0;
			}
		};
		return visitPath(path, fileVisitor);
	}

	@Override
	public int open(String path, int flags, final FuseOpenSetter openSetter) throws FuseException {
		FileVisitor<Integer, Exception> fileVisitor = new FileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				return Errno.EBADF;
			}
			@Override
			public Integer visit(DataFileBean dataFileBean) {
				final File file = dataFileBean.getFile();
				openSetter.setFh(new FileHandle(file));
				return 0;
			}
		};
		return visitPath(path, fileVisitor);
	}

	@Override
	public int read(String path, Object fh, ByteBuffer buf, long offset) throws FuseException {
		FileHandle handle = (FileHandle) fh;
		try {
			handle.getFileChannel().read(buf);
		}
		catch (IOException e) {
			throw new FuseException("Could not read file " + path, e).initErrno(Errno.EIO);
		}
		return 0;
	}

	@Override
	public int readlink(String path, CharBuffer link) throws FuseException {
		return Errno.EBADF;
	}

	@Override
	public int release(String path, Object fh, int flags) throws FuseException {
		((FileHandle) fh).closeQuietly();
		return 0;
	}

	@Override
	public int statfs(FuseStatfsSetter statfsSetter) throws FuseException {
		FileSystemProvider fileSystemProvider = getFileSystemProvider();
		long totalSize = fileSystemProvider.getTotalSize();
		int fileCount = fileSystemProvider.countFiles();
		statfsSetter.set(BLOCK_SIZE, (int) ((totalSize + BLOCK_SIZE - 1) / BLOCK_SIZE), 0, 0, fileCount, 0, NAME_LENGTH);
		return 0;
	}

	private int getMode(FileBean fileBean) {
		FileVisitor<Integer, Exception> fileVisitor = new FileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DataFileBean dataFileBean) {
				return 0444 | FuseFtypeConstants.TYPE_FILE;
			}
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				return 0555 | FuseFtypeConstants.TYPE_DIR;
			}
		};
		return fileBean.accept(fileVisitor);
	}

	protected FileBean getFileBean(String path) throws FuseException {
		try {
			return getFileSystemProvider().findByPath(path);
		}
		catch (FileNotFoundException e) {
			throw new FuseException("Cannot find file " + path).initErrno(Errno.ENOENT);
		}
		catch (IOException e) {
			throw new FuseException("Cannot find file " + path, e).initErrno(Errno.ENOENT);
		}
	}
	
	protected int visitPath(String path, FileVisitor<Integer, ? extends Exception> fileVisitor) throws FuseException {
		FileBean fileBean = getFileBean(path);
		int result = fileBean.accept(fileVisitor);
		if (fileVisitor.getException() != null) {
			throw new FuseException("An unexpected error occurred.", fileVisitor.getException()).initErrno(Errno.ENOENT);
		}
		return result;
	}

	protected FileSystemProvider getFileSystemProvider() {
		return i_fileSystemProvider;
	}

	protected void setFileSystemProvider(FileSystemProvider fileSystemProvider) {
		i_fileSystemProvider = fileSystemProvider;
	}
}
