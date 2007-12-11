package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.flacconverter.FlacAction;
import uk.co.unclealex.music.web.encoded.model.DeviceBean;

public class DeviceAction extends FlacAction {

	private DeviceBean i_device;
	
	public DeviceBean getDevice() {
		return i_device;
	}

	public void setDevice(DeviceBean device) {
		i_device = device;
	}
}
