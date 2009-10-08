package uk.co.unclealex.music.fs;

import java.nio.ByteBuffer;

import fuse.Errno;
import fuse.Filesystem3;
import fuse.FuseException;

public abstract class ReadOnlyFileSystem implements Filesystem3 {

	protected int readOnly() {
		return Errno.EROFS;
	}

	@Override
	public int chmod(String path, int mode) throws FuseException {
		return readOnly();
	}

	@Override
	public int chown(String path, int uid, int gid) throws FuseException {
		return readOnly();
	}

	@Override
	public int link(String from, String to) throws FuseException {
		return readOnly();
	}

	@Override
	public int mkdir(String path, int mode) throws FuseException {
		return readOnly();
	}

	@Override
	public int mknod(String path, int mode, int rdev) throws FuseException {
		return readOnly();
	}

	@Override
	public int rename(String from, String to) throws FuseException {
		return readOnly();
	}

	@Override
	public int rmdir(String path) throws FuseException {
		return readOnly();
	}

	@Override
	public int symlink(String from, String to) throws FuseException {
		return readOnly();
	}

	@Override
	public int truncate(String path, long size) throws FuseException {
		return readOnly();
	}

	@Override
	public int unlink(String path) throws FuseException {
		return readOnly();
	}

	@Override
	public int utime(String path, int atime, int mtime) throws FuseException {
		return readOnly();
	}
	
	@Override
	public int write(String path, Object fh, boolean isWritepage, ByteBuffer buf, long offset) throws FuseException {
		return readOnly();
	}

	public int removexattr(String path, String name) throws FuseException {
		return readOnly();
	}

	public int setxattr(String path, String name, ByteBuffer value, int flags) throws FuseException {
		return readOnly();
	}

}
