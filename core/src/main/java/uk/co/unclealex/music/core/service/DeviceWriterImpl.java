package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.service.DeviceService;
import uk.co.unclealex.music.base.service.DeviceWriter;
import uk.co.unclealex.music.base.service.DevicesWriter;
import uk.co.unclealex.music.base.service.DevicesWriterFactory;
import uk.co.unclealex.music.base.service.ProgressWritingListenerService;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.base.writer.WritingListener;

@Service
@Transactional
public class DeviceWriterImpl implements DeviceWriter {

	private static final Logger log = Logger.getLogger(DeviceWriterImpl.class);
	
	private ProgressWritingListenerService i_progressWritingListenerService;
	private DeviceService i_deviceService;
	private DevicesWriterFactory i_devicesWriterFactory;
	
	@Override
	public void removeMusicFolders(DeviceBean deviceBean, File deviceDirectory) throws IOException {
		if (deviceBean.isDeletingRequired()) {
			log.info("Removing music folders for device " + deviceBean.getDescription());
			removeMusicFolders(deviceBean.getEncoderBean().getExtension(), deviceDirectory);
		}
	}
	
	public void removeMusicFolders(String extension, File deviceDirectory) throws IOException {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		};
		for (File dir : deviceDirectory.listFiles(directoryFilter)) {
			if (containsMusicFile(dir, extension)) {
				FileUtils.deleteDirectory(dir);
			}
		}
	}
	
	protected boolean containsMusicFile(File dir, String extension) {
		boolean found = false;
		File[] files = dir.listFiles();
		for (int idx = 0; !found && idx < files.length; idx++ ) {
			File f = files[idx];
			found =
				(f.isFile() && FilenameUtils.isExtension(f.getName(), extension)) ||
				(f.isDirectory() && containsMusicFile(f, extension));
		}
		return found;
	}

	@Override
	public void writeMusic(
			Map<DeviceBean, File> deviceDirectories, Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException {
		Map<EncoderBean, DevicesWriter> devicesWritersByEncoderBean = new HashMap<EncoderBean, DevicesWriter>();
		for (Map.Entry<DeviceBean, File> entry : deviceDirectories.entrySet()) {
			DeviceBean deviceBean = entry.getKey();
			EncoderBean encoderBean = deviceBean.getEncoderBean();
			File deviceDirectory = entry.getValue();
			Collection<WritingListener> deviceWritingListeners = writingListeners.get(deviceBean);
			DevicesWriter devicesWriter = devicesWritersByEncoderBean.get(encoderBean);
			if (devicesWriter == null) {
				devicesWriter = getDevicesWriterFactory().create();
				devicesWritersByEncoderBean.put(encoderBean, devicesWriter);
			}
			devicesWriter.addDevice(deviceBean, deviceDirectory, deviceWritingListeners);
		}
		
		final TrackWritingException trackWritingException = new TrackWritingException();
		for (DevicesWriter devicesWriter : devicesWritersByEncoderBean.values()) {
			try {
				devicesWriter.write();
			}
			catch (TrackWritingException e) {
				trackWritingException.registerExceptions(e);
			};
		}
		if (trackWritingException.requiresThrowing()) {
			throw trackWritingException;
		}
	}
	
	@Override
	public void writeToDevices(Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException, IOException {
		DeviceService deviceService = getDeviceService();
		Map<DeviceBean, String> pathsByDeviceBean = deviceService.findDevicesAndFiles();
		Map<DeviceBean, File> deviceDirectories = new HashMap<DeviceBean, File>();
		for (DeviceBean deviceBean : new HashSet<DeviceBean>(writingListeners.keySet())) {
			String path = pathsByDeviceBean.get(deviceBean);
			if (path == null) {
				registerIoException(
						new IOException("The device " + deviceBean + " is not connected."), deviceBean, writingListeners);
			}
			else {
				try {
					File mountPoint = deviceService.getMountPointForFile(path);
					deviceDirectories.put(deviceBean, mountPoint);
				}
				catch (IOException e) {
					registerIoException(e, deviceBean, writingListeners);
				}
			}
		}
		writeMusic(deviceDirectories, writingListeners);
	}
	
	protected void registerIoException(IOException exception,
			DeviceBean deviceBean,
			Map<DeviceBean, Collection<WritingListener>> writingListeners) {
		log.error("Device " + deviceBean.getDescription() + " caused an exception.", exception);
		Collection<WritingListener> listeners = writingListeners.get(deviceBean);
		if (listeners != null) {
			for (WritingListener writingListener : listeners) {
				writingListener.finish(exception);
			}
		}
		writingListeners.remove(deviceBean);
	}

	@Override
	public void writeToDevices(Collection<DeviceBean> deviceBeans) throws TrackWritingException, IOException {
		ProgressWritingListenerService progressWritingListenerService = getProgressWritingListenerService();
		Map<DeviceBean, Collection<WritingListener>> writingListeners = new HashMap<DeviceBean, Collection<WritingListener>>();
		for (DeviceBean deviceBean : deviceBeans) {
			Collection<WritingListener> listeners = new ArrayList<WritingListener>();
			listeners.add(progressWritingListenerService.createNewListener(deviceBean));
			writingListeners.put(deviceBean, listeners);
		}
		writeToDevices(writingListeners);
	}
	
	@Override
	public void writeToAllDevices() throws TrackWritingException, IOException {
		writeToDevices(getDeviceService().findDevicesAndFiles().keySet());
	}
	
	public ProgressWritingListenerService getProgressWritingListenerService() {
		return i_progressWritingListenerService;
	}

	@Required
	public void setProgressWritingListenerService(
			ProgressWritingListenerService progressWritingListenerService) {
		i_progressWritingListenerService = progressWritingListenerService;
	}

	public DevicesWriterFactory getDevicesWriterFactory() {
		return i_devicesWriterFactory;
	}

	@Required
	public void setDevicesWriterFactory(DevicesWriterFactory devicesWriterFactory) {
		i_devicesWriterFactory = devicesWriterFactory;
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}
}
