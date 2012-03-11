package uk.co.unclealex.music.legacy;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;


public interface DeviceService {

	public SortedSet<Device> getAllDevices();

	public void createDeviceFileSystems(SortedMap<String, SortedSet<File>> directoriesByOwner, Set<File> flacDirectories);

	public SortedMap<String, File> listDeviceImageFilesByRelativePath(Device device) throws IOException;

	public Device findByName(String deviceName);

	public File getDeviceDirectory(Device device);

	public SortedSet<Device> getAllConnectedDevices();
}
