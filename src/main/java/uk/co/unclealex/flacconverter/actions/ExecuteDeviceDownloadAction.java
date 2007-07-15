package uk.co.unclealex.flacconverter.actions;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;
import uk.co.unclealex.flacconverter.encoded.service.WritingListener;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public class ExecuteDeviceDownloadAction extends DeviceDownloadAction {

	private WritingListener i_writingListener;
	
	@Override
	public String execute(WritingListenerService writingListenerService,
			DeviceService deviceService, DeviceBean deviceBean, String path) throws IOException {
		WritingListener writingListener = null;
		try {
			writingListener = writingListenerService.createNewListener(deviceBean);
			if (writingListener == null) {
				return NONE;
			}
			File mountPoint = deviceService.getMountPointForFile(path);
			deviceService.removeMusicFolders(deviceBean, mountPoint);
			deviceService.writeMusic(deviceBean, mountPoint, writingListener);
			deviceService.safelyRemove(mountPoint);
			return NONE;
		}
		finally {
			if (writingListener != null) {
				writingListenerService.unregisterListener(deviceBean, writingListener);
			}
		}
	}
	
	public WritingListener getWritingListener() {
		return i_writingListener;
	}

	public void setWritingListener(WritingListener writingListener) {
		i_writingListener = writingListener;
	}
}
