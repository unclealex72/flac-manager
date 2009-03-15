package uk.co.unclealex.music.web.commands;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.service.DeviceWriter;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.commands.Command;

@Service
public class DeviceFS implements Command {

	private DeviceWriter i_deviceWriter;
	
	@Override
	public void execute(String[] args) throws IOException, TrackWritingException {
		getDeviceWriter().writeToDeviceAtDirectory(args[0], new File(args[1]));
	}
	
	public DeviceWriter getDeviceWriter() {
		return i_deviceWriter;
	}

	@Required
	public void setDeviceWriter(DeviceWriter deviceWriter) {
		i_deviceWriter = deviceWriter;
	}
}
