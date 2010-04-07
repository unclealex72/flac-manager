package uk.co.unclealex.music.encoding;

import java.io.File;
import java.util.SortedMap;
import java.util.SortedSet;

public interface DeviceService {

	public SortedSet<Device> getAllDevices();

	public void createDeviceFileSystems(SortedMap<String, SortedSet<File>> directoriesByOwner);

}
