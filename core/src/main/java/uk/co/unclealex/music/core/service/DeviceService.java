package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.core.writer.WritingListener;

public interface DeviceService {

	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException;
	
	public File getMountPointForFile(String path) throws IOException;
	
	public boolean mountingRequiresPassword(String path);
	
	public void removeMusicFolders(DeviceBean deviceBean, File deviceDirectory) throws IOException;
	
	public void writeMusic(
			Map<DeviceBean, File> deviceDirectories, Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException;

	public void safelyRemove(File mountPoint) throws IOException;

	public void writeToDevices(Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException, IOException;
	
	public void writeToDevices(Collection<DeviceBean> deviceBeans) throws TrackWritingException, IOException;

	public void writeToAllDevices() throws TrackWritingException, IOException;

}
