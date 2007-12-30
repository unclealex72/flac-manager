package uk.co.unclealex.music.commands;

import uk.co.unclealex.music.core.service.DeviceService;
import uk.co.unclealex.music.core.service.EncodedService;

@uk.co.unclealex.music.core.spring.Main
public class WriteToDevices extends Main {

	private DeviceService i_deviceService;
	private EncodedService i_encodedService;
	
	@Override
	public void execute() throws Exception {
		getEncodedService().updateAllFilenames();
		getDeviceService().writeToAllDevices();
	}

	public static void main(String[] args) throws Exception {
		Main.execute(new WriteToDevices());
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}
}
