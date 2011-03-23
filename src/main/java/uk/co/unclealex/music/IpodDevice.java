package uk.co.unclealex.music;

import java.io.File;

public class IpodDevice extends FileSystemDevice {

	public IpodDevice(String name, String owner, Encoding encoding, File mountPoint) {
		super(name, owner, encoding, mountPoint, null, true);
	}

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}
}
