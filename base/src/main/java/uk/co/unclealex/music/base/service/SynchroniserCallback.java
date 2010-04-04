package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.DeviceFileBean;

public interface SynchroniserCallback {

	public void deviceInitialised(DeviceBean deviceBean, String initialisationInformation);
	
	public void deviceDestroyed(DeviceBean deviceBean, String destroyedInformation);
	
	public void trackAdded(DeviceBean deviceBean, DeviceFileBean deviceFileBean);
	
	public void trackRemoved(DeviceBean deviceBean, DeviceFileBean deviceFileBean);
}
