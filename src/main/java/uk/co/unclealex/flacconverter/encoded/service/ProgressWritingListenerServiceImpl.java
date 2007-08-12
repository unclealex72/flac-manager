package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public class ProgressWritingListenerServiceImpl implements ProgressWritingListenerService {

	private ConcurrentMap<DeviceBean, WritingListener> i_writingListeners = 
		new ConcurrentHashMap<DeviceBean, WritingListener>();
	
	@Override
	public WritingListener createNewListener(DeviceBean deviceBean) {
		ConcurrentMap<DeviceBean, WritingListener> writingListeners = getWritingListeners();
		WritingListener writingListener = new WritingListener();
		if (writingListeners.putIfAbsent(deviceBean, writingListener) == null) {
			return writingListener;
		}
		else {
			return null;
		}
	}

	@Override
	public SortedMap<DeviceBean, WritingListener> getAllListeners() {
		return new TreeMap<DeviceBean, WritingListener>(getWritingListeners());
	}

	@Override
	public void unregisterListener(DeviceBean deviceBean, WritingListener writingListener) {
		getWritingListeners().remove(deviceBean, writingListener);
	}

	@Override
	public boolean hasWritingListener(DeviceBean deviceBean) {
		return getWritingListeners().containsKey(deviceBean);
	}
	
	public ConcurrentMap<DeviceBean, WritingListener> getWritingListeners() {
		return i_writingListeners;
	}

	protected void setWritingListeners(
			ConcurrentMap<DeviceBean, WritingListener> writingListeners) {
		i_writingListeners = writingListeners;
	}

}
