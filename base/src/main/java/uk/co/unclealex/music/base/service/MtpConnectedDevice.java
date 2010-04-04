package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.MtpDeviceBean;
import uk.co.unclealex.music.base.visitor.ConnectedDeviceVisitor;

public class MtpConnectedDevice extends ConnectedDevice<MtpDeviceBean> {

	
	public MtpConnectedDevice(MtpDeviceBean deviceBean) {
		super(deviceBean);
	}

	public <R, E extends Exception> R accept(ConnectedDeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
}
