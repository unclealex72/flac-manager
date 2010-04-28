package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;

public interface DeviceService {

	public SortedSet<Device> getAllDevices();

	public void createDeviceFileSystems(SortedMap<String, SortedSet<File>> directoriesByOwner);

	public SortedMap<String, File> listDeviceImageFilesByRelativePath(Device device) throws IOException;

	public Device findByName(String deviceName);
}