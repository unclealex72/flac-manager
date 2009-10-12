package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.commons.io.FilenameUtils;

import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.visitor.DaoFileVisitor;
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
    final int atime = (int) (System.currentTimeMillis() / 1000L);
		DaoFileVisitor<Integer, Exception> fileVisitor = new DaoFileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				int ctime = getFileCreationTime(directoryFileBean);
				int mtime = getFileModificationTime(directoryFileBean);
				int childCount = getFileSystemProvider().countChildren(directoryFileBean);
        getattrSetter.set(
            createId(directoryFileBean),
            getMode(directoryFileBean),
            1,
            0, 0,
            0,
            childCount * NAME_LENGTH,
            (childCount * NAME_LENGTH + BLOCK_SIZE - 1) / BLOCK_SIZE,
            atime, mtime, ctime);
        return 0;
			}
			@Override
			public Integer visit(EncodedTrackFileBean encodedTrackFileBean) {
				int ctime = getFileCreationTime(encodedTrackFileBean);
				int mtime = getFileModificationTime(encodedTrackFileBean);
				long fileSize = encodedTrackFileBean.getFile().length();
        getattrSetter.set(
            createId(encodedTrackFileBean),
            getMode(encodedTrackFileBean),
            1,
            0, 0,
            0,
            fileSize,
            (fileSize + BLOCK_SIZE - 1) / BLOCK_SIZE,
            atime, mtime, ctime);
        return 0;
			}
		};
		return visitPath(path, fileVisitor);
	}

	protected int getFileCreationTime(FileBean fileBean) {
		return (int) (fileBean.getCreationTimestamp().getTime() / 1000);
	}

	protected int getFileModificationTime(FileBean fileBean) {
		long reportedLastModificationTime = fileBean.getModificationTimestamp().getTime();
		DaoFileVisitor<Long, Exception> fileVisitor = new DaoFileVisitor<Long, Exception>() {
			@Override
			public Long visit(DirectoryFileBean directoryFileBean) {
				return 0l;
			}
			@Override
			public Long visit(EncodedTrackFileBean encodedTrackFileBean) {
				return encodedTrackFileBean.getEncodedTrackBean().getTrackDataBean().getFile().lastModified();
			}
		};
		long fileLastModificationTime = fileBean.accept(fileVisitor);
		return (int) (Math.max(reportedLastModificationTime, fileLastModificationTime) / 1000l);
	}
	
	@Override
	public int getdir(String path, final FuseDirFiller dirFiller) throws FuseException {
		DaoFileVisitor<Integer, Exception> fileVisitor = new DaoFileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(EncodedTrackFileBean encodedTrackFileBean) {
				return Errno.EBADF;
			}
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				for (FileBean fileBean : getFileSystemProvider().getChildren(directoryFileBean)) {
					dirFiller.add(FilenameUtils.getName(fileBean.getPath()), createId(fileBean), getMode(fileBean));
				}
				return 0;
			}
		};
		return visitPath(path, fileVisitor);
	}

	@Override
	public int open(String path, int flags, final FuseOpenSetter openSetter) throws FuseException {
		DaoFileVisitor<Integer, Exception> fileVisitor = new DaoFileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(DirectoryFileBean directoryFileBean) {
				return Errno.EBADF;
			}
			@Override
			public Integer visit(EncodedTrackFileBean encodedTrackFileBean) {
				final File file = encodedTrackFileBean.getFile();
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
		DaoFileVisitor<Integer, Exception> fileVisitor = new DaoFileVisitor<Integer, Exception>() {
			@Override
			public Integer visit(EncodedTrackFileBean encodedTrackFileBean) {
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
			FileBean fileBean = getFileSystemProvider().findByPath(path.substring(1));
			if (fileBean == null) {
				throw new FuseException("Cannot find file " + path).initErrno(Errno.ENOENT);
			}
			return fileBean;
		}
		catch (IOException e) {
			throw new FuseException("Cannot find file " + path, e).initErrno(Errno.ENOENT);
		}
	}
	
	protected int visitPath(String path, DaoFileVisitor<Integer, ? extends Exception> fileVisitor) throws FuseException {
		FileBean fileBean = getFileBean(path);
		int result = fileBean.accept(fileVisitor);
		if (fileVisitor.getException() != null) {
			throw new FuseException("An unexpected error occurred.", fileVisitor.getException()).initErrno(Errno.ENOENT);
		}
		return result;
	}

	protected long createId(FileBean fileBean) {
		return fileBean.getPath().hashCode();
		
	}
	public FileSystemProvider getFileSystemProvider() {
		return i_fileSystemProvider;
	}

	public void setFileSystemProvider(FileSystemProvider fileSystemProvider) {
		i_fileSystemProvider = fileSystemProvider;
	}
}
