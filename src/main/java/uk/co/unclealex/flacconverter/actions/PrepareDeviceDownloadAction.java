package uk.co.unclealex.flacconverter.actions;

import java.io.IOException;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public class PrepareDeviceDownloadAction extends DeviceDownloadAction {

	@Override
	public String execute(WritingListenerService writingListenerService,
			DeviceService deviceService, DeviceBean deviceBean, String path) throws IOException {
		if (writingListenerService.hasWritingListener(deviceBean)) {
			return "alreadywriting";
		}
		if (deviceService.mountingRequiresPassword(path)) {
			return "requirespassword"; 
		}
		else {
			return SUCCESS;
		}
	}
	
}
