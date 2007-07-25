package uk.co.unclealex.flacconverter.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import uk.co.unclealex.flacconverter.encoded.dao.DeviceDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.WritingListener;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public class MonitorDeviceDownloadAction extends FlacAction implements ServletRequestAware {

	private final Logger log = Logger.getLogger(getClass());

	private DeviceBean i_device;
	private DeviceDao i_deviceDao;
	private String i_errorMessage;
	private String i_stackTrace;
	private WritingListener i_writingListener;
	private HttpServletRequest i_servletRequest;
	
	@Override
	public String execute() throws IOException, InterruptedException {
		DeviceBean deviceBean = getDeviceDao().findById(getDevice().getId());
		HttpServletRequest req = getServletRequest();
		URL u = new URL(
				req.getScheme(), req.getServerName(), req.getServerPort(), 
				req.getContextPath() + "/device-downloading.html?device=" + deviceBean.getId());
		u.openStream();
		WritingListenerService service = getWritingListenerService();
		WritingListener writingListener = service.getAllListeners().get(deviceBean);
		setWritingListener(writingListener);
		writingListener.join();
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

	public DeviceBean getDevice() {
		return i_device;
	}

	public void setDevice(DeviceBean device) {
		i_device = device;
	}

	public WritingListener getWritingListener() {
		return i_writingListener;
	}

	public void setWritingListener(WritingListener writingListener) {
		i_writingListener = writingListener;
	}

	public HttpServletRequest getServletRequest() {
		return i_servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		i_servletRequest = servletRequest;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

}
