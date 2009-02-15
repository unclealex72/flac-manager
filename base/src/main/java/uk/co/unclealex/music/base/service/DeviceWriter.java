package uk.co.unclealex.music.base.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.base.writer.WritingListener;

public interface DeviceWriter {

	public void removeMusicFolders(String extension, File deviceDirectory) throws IOException;
	
	public void removeMusicFolders(DeviceBean deviceBean, File deviceDirectory) throws IOException;
	
	public void writeMusic(
			Map<DeviceBean, File> deviceDirectories, Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException;

	public void writeToDevices(Map<DeviceBean, Collection<WritingListener>> writingListeners) throws TrackWritingException, IOException;
	
	public void writeToDevices(Collection<DeviceBean> deviceBeans) throws TrackWritingException, IOException;

	public void writeToAllDevices() throws TrackWritingException, IOException;

	public boolean containsMusicFile(File directory, String extension);

}
