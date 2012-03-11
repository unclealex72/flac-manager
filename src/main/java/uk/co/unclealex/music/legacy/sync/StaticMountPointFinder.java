package uk.co.unclealex.music.legacy.sync;

import java.io.File;

public class StaticMountPointFinder implements MountPointFinder {

	private final File i_mountPoint;
	
	public StaticMountPointFinder(File mountPoint) {
		super();
		i_mountPoint = mountPoint;
	}

	@Override
	public File findMountPoint() {
		File mountPoint = getMountPoint();
		return mountPoint.exists() && mountPoint.isDirectory()?mountPoint:null;
	}

	public File getMountPoint() {
		return i_mountPoint;
	};
}
