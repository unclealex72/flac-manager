package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.music.core.model.DeviceBean;

public class DeviceAction extends EncodedAction {

	private DeviceBean i_device;
	
	public DeviceBean getDevice() {
		return i_device;
	}

	public void setDevice(DeviceBean device) {
		i_device = device;
	}
}
