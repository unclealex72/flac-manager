package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.Device;
import uk.co.unclealex.music.DeviceService;

public class SynchroniserServiceImpl implements SynchroniserService {

	private static Logger log = LoggerFactory.getLogger(SynchroniserServiceImpl.class);
	
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
		synchronise(device);
	}

	protected void synchronise(Device device) throws IOException {
		Thread.currentThread().setName(device.getName());
		Synchroniser synchroniser = getSynchroniserFactory().createSynchroniser(device);
		synchroniser.synchronise();
	}

	@Override
	public void synchroniseAll() throws IOException {
		SortedSet<Device> allConnectedDevices = getDeviceService().getAllConnectedDevices();
		if (!allConnectedDevices.isEmpty()) {
			final ExecutorService executorService = Executors.newFixedThreadPool(allConnectedDevices.size());
			try {
				Transformer<Device, Future<Object>> transformer = new Transformer<Device, Future<Object>>() {
					@Override
					public Future<Object> transform(final Device device) {
						Runnable runnable = new Runnable() {
							public void run() {
								try {
									synchronise(device);
								}
								catch (IOException e) {
									log.error("Synchronising device " + device.getName() + " failed.", e);
								}
							}
						};
						return executorService.submit(runnable, (Object) null);
					}
				};
				for (Future<Object> future : CollectionUtils.collect(allConnectedDevices, transformer)) {
					try {
						future.get();
					}
					catch (InterruptedException e) {
						// Ignore
					}
					catch (ExecutionException e) {
						// Ignore
					}
				}
			}
			finally {
				executorService.shutdown();
			}
		}
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
