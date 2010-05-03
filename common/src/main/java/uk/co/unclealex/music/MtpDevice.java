package uk.co.unclealex.music;


public class MtpDevice extends AbstractDevice {

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}

}
