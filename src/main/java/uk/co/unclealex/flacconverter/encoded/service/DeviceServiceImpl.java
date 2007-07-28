package uk.co.unclealex.flacconverter.encoded.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.encoded.dao.DeviceDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.writer.TrackWriter;
import uk.co.unclealex.flacconverter.encoded.writer.TrackWriterFactory;
import uk.co.unclealex.flacconverter.process.service.ProcessResult;
import uk.co.unclealex.flacconverter.process.service.ProcessService;

public class DeviceServiceImpl implements DeviceService {

	private static final Logger log = Logger.getLogger(DeviceServiceImpl.class);
	
	private ProcessService i_processService;
	private DeviceDao i_deviceDao;
	private OwnerService i_ownerService;
	private TrackWriterFactory i_trackWriterFactory;
	private EncodedTrackDao i_encodedTrackDao;
	
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
	public boolean mountingRequiresPassword(String path) throws IOException {
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
		removeMusicFolders(deviceBean.getEncoderBean().getExtension(), deviceDirectory);
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
	public void writeMusic(DeviceBean deviceBean, File deviceDirectory, WritingListener writingListener) {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		if (writingListener == null) {
			writingListener = new WritingListener();
		}
		String titleFormat = deviceBean.getTitleFormat();
		KnownSizeIterator<EncodedTrackBean> encodedTrackBeansIter =
			getOwnerService().getOwnedEncodedTracks(deviceBean.getOwnerBean(), deviceBean.getEncoderBean());
		writingListener.initialise(encodedTrackBeansIter.size());
		TrackWriter trackWriter = getTrackWriterFactory().createFileTrackWriter(deviceDirectory);
		try {
			trackWriter.create();
			while (encodedTrackBeansIter.hasNext()) {
				EncodedTrackBean encodedTrackBean = encodedTrackBeansIter.next();
				String fileName = trackWriter.write(encodedTrackBean, titleFormat);
				writingListener.registerFileWrite(fileName);
				encodedTrackDao.dismiss(encodedTrackBean);
			}
			trackWriter.close();
			writingListener.finish();
		}
		catch (IOException e) {
			writingListener.finish(e);
		}
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
}
