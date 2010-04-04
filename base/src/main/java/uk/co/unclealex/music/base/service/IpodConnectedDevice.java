package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.IpodDeviceBean;
import uk.co.unclealex.music.base.visitor.ConnectedDeviceVisitor;

public class IpodConnectedDevice extends ConnectedDevice<IpodDeviceBean> {

	public IpodConnectedDevice(IpodDeviceBean deviceBean) {
		super(deviceBean);
	}

	public <R, E extends Exception> R accept(ConnectedDeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
}
