package uk.co.unclealex.music;

import java.io.File;

public class FileSystemDevice extends AbstractDevice {

	private File i_mountPoint;
	
	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}

	public File getMountPoint() {
		return i_mountPoint;
	}

	public void setMountPoint(File mountPoint) {
		i_mountPoint = mountPoint;
	}
}
