package uk.co.unclealex.music.legacy;

public abstract class IpodAgnosticDeviceVisitor<R> implements DeviceVisitor<R> {

	@Override
	public final R visit(IpodDevice ipodDevice) {
		return visit((AbstractFileSystemDevice) ipodDevice);
	}

	@Override
	public final R visit(FileSystemDevice fileSystemDevice) {
		return visit((AbstractFileSystemDevice) fileSystemDevice);
	}

	protected abstract R visit(AbstractFileSystemDevice fileSystemDevice);
}
