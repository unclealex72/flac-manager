package uk.co.unclealex.music;

import java.io.File;

import com.google.common.base.Strings;

public class FileSystemDevice extends AbstractDevice {

	private String i_relativeMusicRoot;
	private File i_mountPoint;
	private boolean i_removable;
	
	public FileSystemDevice(String name, String owner, Encoding encoding, File mountPoint, String relativeMusicRoot, boolean removable) {
		super(name, owner, encoding);
		relativeMusicRoot = Strings.nullToEmpty(relativeMusicRoot).trim();
		i_mountPoint = mountPoint;
		i_removable = removable;
		i_relativeMusicRoot =  Strings.nullToEmpty(relativeMusicRoot).trim();
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, File mountPoint, String relativeMusicRoot) {
		this(name, owner, encoding, mountPoint, relativeMusicRoot, true);
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, File mountPoint) {
		this(name, owner, encoding, mountPoint, null, true);
	}

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}

	public boolean isRemovable() {
		return i_removable;
	}

	public String getRelativeMusicRoot() {
		return i_relativeMusicRoot;
	}

	public File getMountPoint() {
		return i_mountPoint;
	}
}
