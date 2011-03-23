package uk.co.unclealex.music;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.inject.AllDevices;
import uk.co.unclealex.music.inject.DevicesDirectory;
import uk.co.unclealex.process.ProcessService;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DeviceServiceImpl implements DeviceService {

	private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
	
	private SortedSet<Device> i_allDevices;
	private File i_devicesDirectory;
	private FileService i_fileService;
	private ProcessService i_processService;
	
	@Inject
	protected DeviceServiceImpl(@AllDevices SortedSet<Device> allDevices, @DevicesDirectory File devicesDirectory, FileService fileService, ProcessService processService) {
		super();
		i_allDevices = allDevices;
		i_devicesDirectory = devicesDirectory;
		i_fileService = fileService;
		i_processService = processService;
	}

	@Override
	public void createDeviceFileSystems(SortedMap<String, SortedSet<File>> directoriesByOwner, Set<File> flacDirectories) {
		File devicesDirectory = getDevicesDirectory();
		try {
			for (File deviceDirectory : devicesDirectory.listFiles()) {
				log.info("Clearing device directory " + deviceDirectory);
				delete(deviceDirectory);
			}
			for (Device device : getAllDevices()) {
				createDeviceFileSystem(device, directoriesByOwner, flacDirectories);
			}
		}
		catch (IOException e) {
			log.warn(devicesDirectory + " could not be deleted. Aborting.");
		}
	}

	protected void delete(File file) throws IOException {
		if (!file.getCanonicalPath().equals(file.getAbsolutePath()) || !file.isDirectory()) {
			file.delete();
		}
		else {
			for (File child : file.listFiles()) {
				delete(child);
				file.delete();
			}
		}
	}

	protected void createDeviceFileSystem(Device device, SortedMap<String, SortedSet<File>> directoriesByOwner, Set<File> flacDirectories) {
		String owner = device.getOwner();
		Encoding encoding = device.getEncoding();
		File deviceDirectory = getDeviceDirectory(device);
		if (deviceDirectory.exists()) {
			return;
		}
		SortedSet<File> ownedFlacDirectories = directoriesByOwner.get(owner);
		FileService fileService = getFileService();
		ownedFlacDirectories = fileService.expandOwnedDirectories(ownedFlacDirectories, flacDirectories);
		for (File ownedFlacDirectory : ownedFlacDirectories) {
			File encodedDirectory = fileService.translateFlacDirectoryToEncodedDirectory(ownedFlacDirectory, encoding);
			if (encodedDirectory.exists()) {
				String relativeEncodedPath = fileService.relativiseFile(encodedDirectory);
				File newSymlinkDirectory;
				if (relativeEncodedPath == null) {
					newSymlinkDirectory = deviceDirectory;
				}
				else {
					char firstLetter = fileService.getFirstLetter(relativeEncodedPath);
					newSymlinkDirectory = new File(
							new File(deviceDirectory, Character.toString(firstLetter)), relativeEncodedPath);
				}
				try {
					symLink(newSymlinkDirectory, encodedDirectory);
				}
				catch (IOException e) {
					log.error("Could not create symbolic link directory " + newSymlinkDirectory, e);
				}
			}
		}
	}

	@Override
	public File getDeviceDirectory(Device device) {
		String name = String.format("%s %s", device.getOwner(), device.getEncoding().getExtension());
		File deviceDirectory = new File(getDevicesDirectory(), name);
		return deviceDirectory;
	}

	protected void symLink(File newSymlinkDirectory, File encodedDirectory) throws IOException {
		log.info("Linking " + newSymlinkDirectory);
		newSymlinkDirectory.getParentFile().mkdirs();
		ProcessBuilder processBuilder = 
			new ProcessBuilder("ln", "-s", encodedDirectory.getAbsolutePath(), newSymlinkDirectory.getAbsolutePath());
		getProcessService().run(processBuilder, true);
	}

	@Override
	public SortedMap<String, File> listDeviceImageFilesByRelativePath(Device device) throws IOException {
		SortedMap<String, File> deviceImageFilesByRelativePath = new TreeMap<String, File>();
		listDeviceImageFiles("", getDeviceDirectory(device), deviceImageFilesByRelativePath);
		return deviceImageFilesByRelativePath;
	}
	
	protected void listDeviceImageFiles(String path, File f, SortedMap<String, File> deviceImageFilesByRelativePath) throws IOException {
		f = f.getCanonicalFile();
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				listDeviceImageFiles(path + child.getName() + "/", child, deviceImageFilesByRelativePath);
			}
		}
		else {
			deviceImageFilesByRelativePath.put(path.substring(0, path.length() - 1), f);
		}
	}

	@Override
	public Device findByName(final String deviceName) {
		Predicate<Device> predicate = new Predicate<Device>() {
			@Override
			public boolean apply(Device device) {
				return deviceName.equals(device.getName());
			}
		};
		return Iterables.find(getAllDevices(), predicate, null);
	}
	
	@Override
	public SortedSet<Device> getAllConnectedDevices() {
		Predicate<Device> predicate = new DeviceIsConnectedPredicate();
		return Sets.newTreeSet(Iterables.filter(getAllDevices(), predicate));
	}
	
	protected class DeviceIsConnectedPredicate extends IpodAgnosticDeviceVisitor<Boolean> implements Predicate<Device> {

		@Override
		public boolean apply(Device device) {
			return device.accept(this);
		}

		@Override
		public Boolean visit(FileSystemDevice fileSystemDevice) {
			return fileSystemDevice.getMountPoint().isDirectory();
		}

		@Override
		public Boolean visit(MtpDevice mtpDevice) {
			return false;
		}
	}
	
	public SortedSet<Device> getAllDevices() {
		return i_allDevices;
	}

	public File getDevicesDirectory() {
		return i_devicesDirectory;
	}

	public FileService getFileService() {
		return i_fileService;
	}

	public ProcessService getProcessService() {
		return i_processService;
	}
}
