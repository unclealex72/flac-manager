package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedMap;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.writer.ProgressWritingListener;

public interface ProgressWritingListenerService {

	/**
	 * Create a new writing listener for a device bean.
	 * @param deviceBean
	 * @return The created listener or null if one already exists.
	 */
	public ProgressWritingListener createNewListener(DeviceBean deviceBean);
	
	public SortedMap<DeviceBean, ProgressWritingListener> getAllListeners();
	
	public void unregisterListener(DeviceBean deviceBean, ProgressWritingListener progressProgressWritingListener);

	public boolean hasProgressWritingListener(DeviceBean deviceBean);
}
