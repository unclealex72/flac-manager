package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedMap;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public interface WritingListenerService {

	/**
	 * Create a new writing listener for a device bean.
	 * @param deviceBean
	 * @return The created listener or null if one already exists.
	 */
	public WritingListener createNewListener(DeviceBean deviceBean);
	
	public SortedMap<DeviceBean, WritingListener> getAllListeners();
	
	public void unregisterListener(DeviceBean deviceBean, WritingListener writingListener);

	public boolean hasWritingListener(DeviceBean deviceBean);
}
