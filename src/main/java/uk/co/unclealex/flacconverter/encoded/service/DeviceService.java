package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public interface DeviceService {

	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException;
	
	public File getMountPointForFile(String path) throws IOException;
	
	public boolean mountingRequiresPassword(String path) throws IOException;
	
	public void removeMusicFolders(DeviceBean deviceBean, File deviceDirectory) throws IOException;
	
	public void writeMusic(DeviceBean deviceBean, File deviceDirectory, WritingListener writingListener);

	public void safelyRemove(File mountPoint) throws IOException;

}
