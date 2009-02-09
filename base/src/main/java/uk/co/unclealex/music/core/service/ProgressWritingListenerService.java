package uk.co.unclealex.music.core.service;

import java.util.SortedMap;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.writer.WritingListener;

public interface ProgressWritingListenerService {

	/**
	 * Create a new writing listener for a device bean.
	 * @param deviceBean
	 * @return The created listener or null if one already exists.
	 */
	public WritingListener createNewListener(DeviceBean deviceBean);
	
	public SortedMap<DeviceBean, WritingListener> getAllListeners();
	
	public void unregisterListener(DeviceBean deviceBean, WritingListener writingListener);

	public boolean hasProgressWritingListener(DeviceBean deviceBean);
}