package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.service.ConnectedDevice;
import uk.co.unclealex.music.base.service.FileSystemConnectedDevice;
import uk.co.unclealex.music.base.service.IpodConnectedDevice;
import uk.co.unclealex.music.base.service.MtpConnectedDevice;

public abstract class ConnectedDeviceVisitor<R, E extends Exception> extends Visitor<E> {

	public final R visit(ConnectedDevice<?> connectedDevice) {
		throw new IllegalArgumentException("Unknown connected device type: " + connectedDevice.getClass());
	}

	public abstract R visit(MtpConnectedDevice mtpConnectedDevice);

	public abstract R visit(IpodConnectedDevice ipodConnectedDevice);

	public abstract R visit(FileSystemConnectedDevice fileSystemConnectedDevice);
}
