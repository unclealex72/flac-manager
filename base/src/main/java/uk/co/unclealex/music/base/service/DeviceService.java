package uk.co.unclealex.music.base.service;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import uk.co.unclealex.music.base.model.DeviceBean;

public interface DeviceService {

	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException;
	
	public File getMountPointForFile(String path) throws IOException;
	
	public boolean mountingRequiresPassword(String path);
	
	public void safelyRemove(File mountPoint) throws IOException;
}
