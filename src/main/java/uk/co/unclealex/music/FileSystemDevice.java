package uk.co.unclealex.music;

import uk.co.unclealex.music.sync.MountPointFinder;

import com.google.common.base.Strings;

public class FileSystemDevice extends AbstractDevice {

	private final String i_relativeMusicRoot;
	private final MountPointFinder i_mountPointFinder;
	private final boolean i_removable;
	
	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder, String relativeMusicRoot, boolean arePlaylistsAvailable, boolean removable) {
		super(name, owner, encoding, arePlaylistsAvailable);
		relativeMusicRoot = Strings.nullToEmpty(relativeMusicRoot).trim();
		i_mountPointFinder = mountPointFinder;
		i_removable = removable;
		i_relativeMusicRoot =  Strings.nullToEmpty(relativeMusicRoot).trim();
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder, String relativeMusicRoot, boolean arePlaylistsAvailable) {
		this(name, owner, encoding, mountPointFinder, relativeMusicRoot, arePlaylistsAvailable, true);
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder, boolean arePlaylistsAvailable) {
		this(name, owner, encoding, mountPointFinder, null, arePlaylistsAvailable, true);
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

	public MountPointFinder getMountPointFinder() {
		return i_mountPointFinder;
	}
}
