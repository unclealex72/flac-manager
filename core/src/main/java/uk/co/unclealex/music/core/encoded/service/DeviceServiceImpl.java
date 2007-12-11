package uk.co.unclealex.music.core.encoded.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.service.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.dao.DeviceDao;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.encoded.model.DeviceBean;
import uk.co.unclealex.music.core.encoded.model.EncoderBean;
import uk.co.unclealex.music.core.encoded.writer.TrackWriterFactory;
import uk.co.unclealex.music.core.encoded.writer.TrackWritingException;
import uk.co.unclealex.music.core.encoded.writer.WritingListener;
import uk.co.unclealex.music.core.process.service.ProcessResult;
import uk.co.unclealex.music.core.process.service.ProcessService;

@Transactional
public class DeviceServiceImpl implements DeviceService {

	private static final Logger log = Logger.getLogger(DeviceServiceImpl.class);
	
	private ProcessService i_processService;
	private ProgressWritingListenerService i_progressWritingListenerService;
	private DeviceDao i_deviceDao;
	private OwnerService i_ownerService;
	private TrackWriterFactory i_trackWriterFactory;
	private EncodedTrackDao i_encodedTrackDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private DevicesWriterFactory i_devicesWriterFactory;
	@Override
	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException {
		SortedMap<DeviceBean, String> devicesAndFiles = new TreeMap<DeviceBean, String>();
		ProcessResult processResult = getProcessService().run(new ProcessBuilder("lsscsi"), true);
		List<String> lines = readStream(new StringReader(processResult.getOutput()));
		for (DeviceBean deviceBean : getDeviceDao().getAll()) {
			String line = findLineContainingString(lines, deviceBean.getIdentifier());
			if (line != null) {
				String[] parts = StringUtils.split(line, ' ');
				String file = parts[parts.length - 1] + "1";
				log.info("Found " + deviceBean.getFullDescription() + " at location " + file);
				devicesAndFiles.put(deviceBean, file);
			}
		}
		return devicesAndFiles;
	}

	@Override
	public File getMountPointForFile(String path) throws IOException {
		ProcessService processService = getProcessService();
		File mountPoint = findExistingMountPoint(path);
		if (mountPoint != null) {
			if (mountPoint.canWrite()) {
				return mountPoint;
			}
			processService.run(new ProcessBuilder("sudo", "umount", path), true);
		}
		processService.run(new ProcessBuilder("pmount", path), true);
		mountPoint = findExistingMountPoint(path);
		if (mountPoint == null) {
			throw new IOException("Mounting " + path + " failed.");
		}
		return mountPoint;
	}

	@Override
	public boolean mountingRequiresPassword(String path) {
		return false;
	}
	
	protected File findExistingMountPoint(String path) throws IOException {
		FileReader reader = new FileReader(new File("/etc/mtab"));
		List<String> entries = readStream(reader);
		reader.close();
		String entry = findLineContainingString(entries, path);
		return entry==null?null:new File(StringUtils.split(entry, ' ')[1]);
	}
	
	@Override
	public void safelyRemove(File mountPoint) throws IOException {
		getProcessService().run(new ProcessBuilder("pumount", mountPoint.getCanonicalPath()), true);
	}

	protected List<String> readStream(Reader in) throws IOException {
		List<String> lines = new LinkedList<String>();
		String line;
		BufferedReader buff = new BufferedReader(in);
		while ((line = buff.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
	
	protected String findLineContainingString(List<String> lines, final String str) {
		Predicate<String> predicate = new Predicate<String>() {
			@Override
			public boolean evaluate(String line) {
				return line.indexOf(str) != -1;
			}
		};
		return CollectionUtils.find(lines, predicate);
	}

	@Override
	public void removeMusicFolders(DeviceBean deviceBean, File deviceDirectory) throws IOException {
		if (deviceBean.isDeletingRequired()) {
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
		List<Thread> threads = new LinkedList<Thread>();
		for (final DevicesWriter devicesWriter : devicesWritersByEncoderBean.values()) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						devicesWriter.write();
					}
					catch (TrackWritingException e) {
						trackWritingException.registerExceptions(e);
					}
				}
			};
			threads.add(thread);
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				log.warn("A track writing thread was interrupted.", e);
			}
		}
		if (trackWritingException.requiresThrowing()) {
			throw trackWritingException;
		}
	}
	
	@Override
	public void writeToDevices(Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException, IOException {
		Map<DeviceBean, String> pathsByDeviceBean = findDevicesAndFiles();
		Map<DeviceBean, File> deviceDirectories = new HashMap<DeviceBean, File>();
		for (DeviceBean deviceBean : new HashSet<DeviceBean>(writingListeners.keySet())) {
			String path = pathsByDeviceBean.get(deviceBean);
			if (path == null) {
				registerIoException(
						new IOException("The device " + deviceBean + " is not connected."), deviceBean, writingListeners);
			}
			else {
				try {
					File mountPoint = getMountPointForFile(path);
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
		Collection<WritingListener> listeners = writingListeners.get(deviceBean);
		if (listeners != null) {
			for (WritingListener writingListener : listeners) {
				writingListener.finish(exception);
			}
		}
		writingListeners.remove(deviceBean);
	}

	@SuppressWarnings("unchecked")
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
		writeToDevices(findDevicesAndFiles().keySet());
	}
	
	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public TrackWriterFactory getTrackWriterFactory() {
		return i_trackWriterFactory;
	}

	public void setTrackWriterFactory(TrackWriterFactory trackWriterFactory) {
		i_trackWriterFactory = trackWriterFactory;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public ProgressWritingListenerService getProgressWritingListenerService() {
		return i_progressWritingListenerService;
	}

	public void setProgressWritingListenerService(
			ProgressWritingListenerService progressWritingListenerService) {
		i_progressWritingListenerService = progressWritingListenerService;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

	public DevicesWriterFactory getDevicesWriterFactory() {
		return i_devicesWriterFactory;
	}

	public void setDevicesWriterFactory(DevicesWriterFactory devicesWriterFactory) {
		i_devicesWriterFactory = devicesWriterFactory;
	}
}