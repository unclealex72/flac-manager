package uk.co.unclealex.music.legacy;

import com.google.inject.Inject;


public class MtpDevice extends AbstractDevice {

	@Inject
	public MtpDevice(String name, String owner, Encoding encoding, boolean arePlaylistsAvailable) {
		super(name, owner, encoding, arePlaylistsAvailable);
	}

	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit(this);
	}
}
