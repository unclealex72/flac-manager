package uk.co.unclealex.music;

import com.google.inject.Inject;


public class MtpDevice extends AbstractDevice {

	@Inject
	public MtpDevice(String name, String owner, Encoding encoding) {
		super(name, owner, encoding);
	}

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}
}
