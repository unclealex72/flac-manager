package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.service.DeviceWriter;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.commands.Command;

@Service
@Transactional(rollbackFor=Exception.class)
public class WriteToDevices implements Command {

	private DeviceWriter i_deviceWriter;
	private EncodedService i_encodedService;
	
	@Override
	public void execute(String[] args) throws TrackWritingException, IOException {
		getEncodedService().updateAllFilenames();
		getDeviceWriter().writeToAllDevices();
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public DeviceWriter getDeviceWriter() {
		return i_deviceWriter;
	}

	public void setDeviceWriter(DeviceWriter deviceWriter) {
		i_deviceWriter = deviceWriter;
	}
}
