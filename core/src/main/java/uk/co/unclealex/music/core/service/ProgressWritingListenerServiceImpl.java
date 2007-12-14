package uk.co.unclealex.music.core.service;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.writer.ProgressWritingListener;

@Service
public class ProgressWritingListenerServiceImpl implements ProgressWritingListenerService {

	private ConcurrentMap<DeviceBean, ProgressWritingListener> i_progressWritingListeners = 
		new ConcurrentHashMap<DeviceBean, ProgressWritingListener>();
	
	@Override
	public ProgressWritingListener createNewListener(DeviceBean deviceBean) {
		ConcurrentMap<DeviceBean, ProgressWritingListener> progressWritingListeners = getProgressWritingListeners();
		ProgressWritingListener progressWritingListener = new ProgressWritingListener();
		if (progressWritingListeners.putIfAbsent(deviceBean, progressWritingListener) == null) {
			return progressWritingListener;
		}
		else {
			return null;
		}
	}

	@Override
	public SortedMap<DeviceBean, ProgressWritingListener> getAllListeners() {
		return new TreeMap<DeviceBean, ProgressWritingListener>(getProgressWritingListeners());
	}

	@Override
	public void unregisterListener(DeviceBean deviceBean, ProgressWritingListener progressWritingListener) {
		getProgressWritingListeners().remove(deviceBean, progressWritingListener);
	}

	@Override
	public boolean hasProgressWritingListener(DeviceBean deviceBean) {
		return getProgressWritingListeners().containsKey(deviceBean);
	}
	
	public ConcurrentMap<DeviceBean, ProgressWritingListener> getProgressWritingListeners() {
		return i_progressWritingListeners;
	}

	protected void setProgressWritingListeners(
			ConcurrentMap<DeviceBean, ProgressWritingListener> progressWritingListeners) {
		i_progressWritingListeners = progressWritingListeners;
	}

}
