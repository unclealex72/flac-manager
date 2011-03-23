package uk.co.unclealex.music;

public abstract class IpodAgnosticDeviceVisitor<R> implements DeviceVisitor<R> {

	@Override
	public final R visit(IpodDevice ipodDevice) {
		return visit((FileSystemDevice) ipodDevice);
	}
}
