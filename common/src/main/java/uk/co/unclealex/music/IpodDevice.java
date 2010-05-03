package uk.co.unclealex.music;



public class IpodDevice extends FileSystemDevice {

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}
}
