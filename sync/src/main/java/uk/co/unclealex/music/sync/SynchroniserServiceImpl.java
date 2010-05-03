package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.music.Device;
import uk.co.unclealex.music.DeviceService;

public class SynchroniserServiceImpl implements SynchroniserService {

	private DeviceService i_deviceService;
	private SynchroniserFactory i_synchroniserFactory;
	
	public void synchronise(String deviceName) throws IOException {
		DeviceService deviceService = getDeviceService();
		Device device = deviceService.findByName(deviceName);
		if (device == null) {
			Transformer<Device, String> transformer = new Transformer<Device, String>() {
				@Override
				public String transform(Device device) {
					return device.getName();
				}
			};
			SortedSet<String> deviceNames = 
				CollectionUtils.collect(deviceService.getAllDevices(), transformer, new TreeSet<String>());
			throw 
				new IllegalArgumentException(
					deviceName + " is not a valid device. Valid devices are: " + StringUtils.join(deviceNames, ", "));
		}
		Synchroniser synchroniser = getSynchroniserFactory().createSynchroniser(device);
		synchroniser.synchronise();
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public SynchroniserFactory getSynchroniserFactory() {
		return i_synchroniserFactory;
	}

	public void setSynchroniserFactory(SynchroniserFactory synchroniserFactory) {
		i_synchroniserFactory = synchroniserFactory;
	}
	
}
