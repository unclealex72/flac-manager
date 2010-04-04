package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.visitor.ConnectedDeviceVisitor;

public abstract class ConnectedDevice<D extends DeviceBean> implements Comparable<ConnectedDevice<D>> {

	private D i_deviceBean;

	public ConnectedDevice(D deviceBean) {
		i_deviceBean = deviceBean;
	}
	
	public <R, E extends Exception> R accept(ConnectedDeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}

	@Override
	public int compareTo(ConnectedDevice<D> o) {
		return getDeviceBean().compareTo(o.getDeviceBean());
	}
	
	public D getDeviceBean() {
		return i_deviceBean;
	}
}
