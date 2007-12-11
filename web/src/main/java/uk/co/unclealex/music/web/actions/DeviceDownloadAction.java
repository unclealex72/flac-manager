package uk.co.unclealex.music.web.actions;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.flacconverter.FlacAction;
import uk.co.unclealex.music.web.encoded.dao.DeviceDao;
import uk.co.unclealex.music.web.encoded.model.DeviceBean;
import uk.co.unclealex.music.web.encoded.service.ProgressWritingListenerService;
import uk.co.unclealex.music.web.encoded.writer.ProgressWritingListener;
import uk.co.unclealex.music.web.encoded.writer.TrackWritingException;
import uk.co.unclealex.music.web.encoded.writer.WritingListener;

public class DeviceDownloadAction extends FlacAction {

	private List<String> i_devices;
	private DeviceDao i_deviceDao;
	
	private SortedMap<DeviceBean, ProgressWritingListener> i_progressWritingListenersByDeviceBean;
	
	@Override
	public String execute() throws TrackWritingException, IOException {
		
		final DeviceDao deviceDao = getDeviceDao();
		Collection<DeviceBean> deviceBeans;
		List<String> devices = getDevices();
		if (devices == null || devices.isEmpty()) {
			deviceBeans = getDeviceService().findDevicesAndFiles().keySet();
		}
		else {
			Transformer<String, DeviceBean> transformer = new Transformer<String, DeviceBean>() {
				@Override
				public DeviceBean transform(String input) {
					return deviceDao.findById(Integer.parseInt(input));
				}
			};
			deviceBeans = CollectionUtils.collect(getDevices(), transformer);
		}
		ProgressWritingListenerService progressWritingListenerService = getProgressWritingListenerService();
		SortedMap<DeviceBean, Collection<WritingListener>> writingListenersByDevicebean =
			new TreeMap<DeviceBean, Collection<WritingListener>>();
		SortedMap<DeviceBean, ProgressWritingListener> progressWritingListenersByDeviceBean = 
			new TreeMap<DeviceBean, ProgressWritingListener>();
		for (DeviceBean device : deviceBeans) {
			DeviceBean deviceBean = deviceDao.findById(device.getId());
			ProgressWritingListener progressWritingListener = progressWritingListenerService.createNewListener(deviceBean);
			if (progressWritingListener != null) {
				List<WritingListener> writingListeners = new LinkedList<WritingListener>();
				writingListeners.add(progressWritingListener);
				writingListenersByDevicebean.put(deviceBean, writingListeners);
				progressWritingListenersByDeviceBean.put(deviceBean, progressWritingListener);
			}
		}
		getDeviceService().writeToDevices(writingListenersByDevicebean);
		return SUCCESS;
	}

	public List<String> getDevices() {
		return i_devices;
	}

	public void setDevices(List<String> devices) {
		i_devices = devices;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

	public SortedMap<DeviceBean, ProgressWritingListener> getProgressWritingListenersByDeviceBean() {
		return i_progressWritingListenersByDeviceBean;
	}

	public void setProgressWritingListenersByDeviceBean(
			SortedMap<DeviceBean, ProgressWritingListener> progressWritingListenersByDeviceBean) {
		i_progressWritingListenersByDeviceBean = progressWritingListenersByDeviceBean;
	}
}
