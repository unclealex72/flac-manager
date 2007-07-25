package uk.co.unclealex.flacconverter.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.encoded.dao.DeviceDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;
import uk.co.unclealex.flacconverter.encoded.service.WritingListener;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public class ExecuteDeviceDownloadAction extends DeviceDownloadAction {

	private static final Logger log = Logger.getLogger(ExecuteDeviceDownloadAction.class);
	
	private WritingListener i_writingListener;
	private DeviceDao i_deviceDao;
	
	@Override
	public String execute(WritingListenerService writingListenerService,
			DeviceService deviceService, DeviceBean deviceBean, String path) throws IOException {
		deviceBean = getDeviceDao().findById(deviceBean.getId());
		WritingListener writingListener = null;
		try {
			writingListener = writingListenerService.createNewListener(deviceBean);
			if (writingListener == null) {
				return "alreadywriting";
			}
			setWritingListener(writingListener);
			File mountPoint = deviceService.getMountPointForFile(path);
			deviceService.removeMusicFolders(deviceBean, mountPoint);
			deviceService.writeMusic(deviceBean, mountPoint, writingListener);
			deviceService.safelyRemove(mountPoint);
			IOException e = writingListener.getException();
			if (e == null) {
				return SUCCESS;
			}
			else {
				log.error("An error occured whilst trying to download to a device.", e);
				setErrorMessage(e.getMessage());
				StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				setStackTrace(stackTrace.toString());
				return ERROR;
			}
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

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}
}
