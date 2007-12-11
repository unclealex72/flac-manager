package uk.co.unclealex.music.core.mains;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.service.DeviceService;

@Transactional
public class WriteToDevices extends Main {

	private DeviceService i_deviceService;
	
	@Override
	public void execute() throws Exception {
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
}
