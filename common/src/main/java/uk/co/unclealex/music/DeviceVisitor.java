package uk.co.unclealex.music;

public interface DeviceVisitor<R> {

	public R visit(IpodDevice ipodDevice);

	public R visit(FileSystemDevice fileSystemDevice);

	public R visit(MtpDevice mtpDevice);
}
