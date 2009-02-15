package uk.co.unclealex.music.commands;

import uk.co.unclealex.music.base.service.DeviceWriter;
import uk.co.unclealex.music.base.service.EncodedService;

@uk.co.unclealex.spring.Main
public class WriteToDevices extends Main {

	private DeviceWriter i_deviceWriter;
	private EncodedService i_encodedService;
	
	@Override
	public void execute() throws Exception {
		getEncodedService().updateAllFilenames();
		getDeviceWriter().writeToAllDevices();
	}

	public static void main(String[] args) throws Exception {
		Main.execute(new WriteToDevices());
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public DeviceWriter getDeviceWriter() {
		return i_deviceWriter;
	}

	public void setDeviceWriter(DeviceWriter deviceWriter) {
		i_deviceWriter = deviceWriter;
	}
}
