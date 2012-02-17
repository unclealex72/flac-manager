package uk.co.unclealex.music;

import uk.co.unclealex.music.sync.MountPointFinder;

public class FileSystemDevice extends AbstractFileSystemDevice {

	
	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder,
			boolean arePlaylistsAvailable) {
		super(name, owner, encoding, mountPointFinder, arePlaylistsAvailable);
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder,
			String relativeMusicRoot, boolean arePlaylistsAvailable, boolean removable) {
		super(name, owner, encoding, mountPointFinder, relativeMusicRoot, arePlaylistsAvailable, removable);
	}

	public FileSystemDevice(String name, String owner, Encoding encoding, MountPointFinder mountPointFinder,
			String relativeMusicRoot, boolean arePlaylistsAvailable) {
		super(name, owner, encoding, mountPointFinder, relativeMusicRoot, arePlaylistsAvailable);
	}

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}
}
