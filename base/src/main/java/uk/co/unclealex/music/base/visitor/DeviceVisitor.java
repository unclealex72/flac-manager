package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.FileSystemDeviceBean;
import uk.co.unclealex.music.base.model.IpodDeviceBean;
import uk.co.unclealex.music.base.model.MtpDeviceBean;

public abstract class DeviceVisitor<R, E extends Exception> extends Visitor<E> {

	public final R visit(DeviceBean deviceBean) {
		throw new IllegalArgumentException("Unknown device type: " + deviceBean.getClass());
	}

	public abstract R visit(IpodDeviceBean ipodDeviceBean);

	public abstract R visit(MtpDeviceBean mtpDeviceBean);

	public abstract R visit(FileSystemDeviceBean fileSystemDeviceBean);
}
