package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.Device;
import uk.co.unclealex.music.DeviceService;
import uk.co.unclealex.process.NamedRunnable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class SynchroniserServiceImpl implements SynchroniserService {

	private static Logger log = LoggerFactory.getLogger(SynchroniserServiceImpl.class);
	
	private DeviceService i_deviceService;
	private SynchroniserFactory i_synchroniserFactory;
	private ExecutorService i_executorService;
	
	@Inject
	protected SynchroniserServiceImpl(DeviceService deviceService, SynchroniserFactory synchroniserFactory, ExecutorService executorService) {
		i_deviceService = deviceService;
		i_synchroniserFactory = synchroniserFactory;
		i_executorService = executorService;
	}

	public void synchronise(String deviceName) throws IOException {
		DeviceService deviceService = getDeviceService();
		Device device = deviceService.findByName(deviceName);
		if (device == null) {
			Function<Device, String> function = new Function<Device, String>() {
				@Override
				public String apply(Device device) {
					return device.getName();
				}
			};
			SortedSet<String> deviceNames = Sets.newTreeSet(Iterables.transform(deviceService.getAllDevices(), function));
			throw 
				new IllegalArgumentException(
					deviceName + " is not a valid device. Valid devices are: " + Joiner.on(", ").join(deviceNames));
		}
		synchronise(device);
	}

	protected void synchronise(Device device) throws IOException {
		Synchroniser synchroniser = getSynchroniserFactory().createSynchroniser(device);
		synchroniser.synchronise();
	}

	@Override
	public void synchroniseAll() throws IOException {
		SortedSet<Device> allConnectedDevices = getDeviceService().getAllConnectedDevices();
		final ExecutorService executorService = getExecutorService();
		Function<Device, Future<?>> function = new Function<Device, Future<?>>() {
			@Override
			public Future<?> apply(final Device device) {
				NamedRunnable runnable = new NamedRunnable() {
					@Override
					public void runAfterName() throws IOException {
						synchronise(device);
					}
					@Override
					public String getThreadName() {
						return device.getName() + " synchronizer";						
					}
					@Override
					public void onError(Throwable t) {
						log.error("Synchronising device " + device.getName() + " failed.", t);
						
					}
				};
				return executorService.submit(runnable);
			}
		};
		for (Future<?> future : Iterables.transform(allConnectedDevices, function)) {
			try {
				future.get();
			}
			catch (Throwable t) {
				log.error("Synchronising failed.", t);
			}
		}
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public SynchroniserFactory getSynchroniserFactory() {
		return i_synchroniserFactory;
	}

	public ExecutorService getExecutorService() {
		return i_executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		i_executorService = executorService;
	}
}
