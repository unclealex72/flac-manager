package uk.co.unclealex.flacconverter.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public abstract class DeviceDownloadAction extends WritingListenersAction {

	private final Logger log = Logger.getLogger(getClass());
	
	private DeviceService i_deviceService;
	private DeviceBean i_device;
	private String i_errorMessage;
	private String i_stackTrace;

	public abstract String execute(
			WritingListenerService writingListenerService, DeviceService deviceService, DeviceBean deviceBean, String path) 
	throws IOException;
	
	@Override
	public String execute() {
		try {
			DeviceService deviceService = getDeviceService();
			DeviceBean deviceBean = getDevice();
			return execute(
					getWritingListenerService(), deviceService, deviceBean, deviceService.findDevicesAndFiles().get(deviceBean));
		}
		catch (IOException e) {
			log.error("An error occured whilst trying to download to a device.", e);
			setErrorMessage(e.getMessage());
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			setStackTrace(stackTrace.toString());
			return ERROR;
		}
	}
	
	public DeviceBean getDevice() {
		return i_device;
	}

	public void setDevice(DeviceBean device) {
		i_device = device;
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public String getErrorMessage() {
		return i_errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		i_errorMessage = errorMessage;
	}

	public String getStackTrace() {
		return i_stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		i_stackTrace = stackTrace;
	}
	
}
