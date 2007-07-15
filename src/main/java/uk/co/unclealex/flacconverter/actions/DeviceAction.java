package uk.co.unclealex.flacconverter.actions;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public class DeviceAction extends FlacAction {

	private DeviceBean i_device;
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}

	public DeviceBean getDevice() {
		return i_device;
	}

	public void setDevice(DeviceBean device) {
		i_device = device;
	}
}
