package uk.co.unclealex.music.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.co.unclealex.music.core.process.service.ProcessServiceImpl;
import uk.co.unclealex.music.core.service.DeviceServiceImpl;

public class DeviceServiceManualTest {

	public static void main(String[] args) throws IOException {
		String device = "/dev/sdi1";
		DeviceServiceImpl deviceServiceImpl = new DeviceServiceImpl();
		deviceServiceImpl.setProcessService(new ProcessServiceImpl());
		@SuppressWarnings("unused")
		String password = null;
		if (deviceServiceImpl.mountingRequiresPassword(device)) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Please enter the root password: ");
			password = reader.readLine();
		}
		File mountPoint = deviceServiceImpl.getMountPointForFile(device);
		for (String path : mountPoint.list()) {
			System.out.println(path);
		}
		deviceServiceImpl.safelyRemove(mountPoint);
	}
}
