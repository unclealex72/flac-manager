package uk.co.unclealex.music.core.service;

import java.io.File;
import java.util.Collection;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.core.writer.WritingListener;

public interface DevicesWriter {

	public void addDevice(DeviceBean deviceBean, File deviceDirectory, Collection<WritingListener> writingListeners);
	
	public void write() throws TrackWritingException;
}
