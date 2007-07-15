package uk.co.unclealex.flacconverter.actions;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;

public class ConnectedDevicesAction extends WritingListenersAction {

	private DeviceService i_deviceService;
	private SortedSet<DeviceBean> i_connectedDevices;
	
	@Override
	public String execute() throws IOException {
		Set<DeviceBean> deviceBeans = getDeviceService().findDevicesAndFiles().keySet();
		SortedSet<DeviceBean> connectedDevices = new TreeSet<DeviceBean>(deviceBeans);
		connectedDevices.removeAll(getWritingListenerService().getAllListeners().keySet());
		setConnectedDevices(connectedDevices);
		return SUCCESS;
	}

	@Required
	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public SortedSet<DeviceBean> getConnectedDevices() {
		return i_connectedDevices;
	}

	public void setConnectedDevices(SortedSet<DeviceBean> deviceBeans) {
		i_connectedDevices = deviceBeans;
	}
}
