package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import uk.co.unclealex.music.ProcessService;

public class DeviceServiceImpl implements DeviceService {

	private static final Logger log = Logger.getLogger(DeviceServiceImpl.class);
	
	private SortedSet<Device> i_allDevices;
	private File i_devicesDirectory;
	private FileService i_fileService;
	private ProcessService i_processService;
	
	@Override
	public void createDeviceFileSystems(SortedMap<String, SortedSet<File>> directoriesByOwner) {
		File devicesDirectory = getDevicesDirectory();
		try {
			for (File deviceDirectory : devicesDirectory.listFiles()) {
				log.info("Clearing device directory " + deviceDirectory);
				delete(deviceDirectory);
			}
			for (Device device : getAllDevices()) {
				createDeviceFileSystem(device, directoriesByOwner);
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

	protected void createDeviceFileSystem(Device device, SortedMap<String, SortedSet<File>> directoriesByOwner) {
		String owner = device.getOwner();
		Encoding encoding = device.getEncoding();
		SortedSet<File> ownedFlacDirectories = directoriesByOwner.get(owner);
		FileService fileService = getFileService();
		File deviceDirectory = new File(getDevicesDirectory(), device.getName());
		for (File ownedFlacDirectory : ownedFlacDirectories) {
			File encodedDirectory = fileService.translateFlacDirectoryToEncodedDirectory(ownedFlacDirectory, encoding);
			if (encodedDirectory.exists()) {
				String relativeEncodedPath = fileService.relativiseFile(encodedDirectory);
				File newSymlinkDirectory = 
					relativeEncodedPath==null?deviceDirectory:new File(deviceDirectory, relativeEncodedPath);
				try {
					symLink(newSymlinkDirectory, encodedDirectory);
				}
				catch (IOException e) {
					log.error("Could not create symbolic link directory " + newSymlinkDirectory, e);
				}
			}
		}
	}

	protected void symLink(File newSymlinkDirectory, File encodedDirectory) throws IOException {
		log.info("Linking " + newSymlinkDirectory);
		newSymlinkDirectory.getParentFile().mkdirs();
		ProcessBuilder processBuilder = 
			new ProcessBuilder("ln", "-s", encodedDirectory.getAbsolutePath(), newSymlinkDirectory.getAbsolutePath());
		getProcessService().run(processBuilder, true);
	}

	public SortedSet<Device> getAllDevices() {
		return i_allDevices;
	}

	public void setAllDevices(SortedSet<Device> allDevices) {
		i_allDevices = allDevices;
	}

	public File getDevicesDirectory() {
		return i_devicesDirectory;
	}

	public void setDevicesDirectory(File devicesDirectory) {
		i_devicesDirectory = devicesDirectory;
	}

	public FileService getFileService() {
		return i_fileService;
	}

	public void setFileService(FileService fileService) {
		i_fileService = fileService;
	}

	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

}
