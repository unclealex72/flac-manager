package uk.co.unclealex.music.core.service;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.service.ProgressWritingListenerService;
import uk.co.unclealex.music.base.writer.WritingListener;
import uk.co.unclealex.music.core.writer.ProgressWritingListener;

@Service
public class ProgressWritingListenerServiceImpl implements ProgressWritingListenerService {

	private ConcurrentMap<DeviceBean, ProgressWritingListener> i_progressWritingListeners = 
		new ConcurrentHashMap<DeviceBean, ProgressWritingListener>();
	
	@Override
	public WritingListener createNewListener(DeviceBean deviceBean) {
		ConcurrentMap<DeviceBean, ProgressWritingListener> progressWritingListeners = getProgressWritingListeners();
		ProgressWritingListener progressWritingListener = new ProgressWritingListener();
		ProgressWritingListener existingProgressWritingListener = progressWritingListeners.putIfAbsent(deviceBean, progressWritingListener);
		if (existingProgressWritingListener == null) {
			return progressWritingListener;
		}
		else {
			return existingProgressWritingListener;
		}
	}

	@Override
	public SortedMap<DeviceBean, WritingListener> getAllListeners() {
		return new TreeMap<DeviceBean, WritingListener>(getProgressWritingListeners());
	}

	@Override
	public void unregisterListener(DeviceBean deviceBean, WritingListener progressWritingListener) {
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
