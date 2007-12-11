package uk.co.unclealex.music.core.encoded.service;

import java.io.File;
import java.util.Collection;

import uk.co.unclealex.music.core.encoded.model.DeviceBean;
import uk.co.unclealex.music.core.encoded.writer.TrackWritingException;
import uk.co.unclealex.music.core.encoded.writer.WritingListener;

public interface DevicesWriter {

	public void addDevice(DeviceBean deviceBean, File deviceDirectory, Collection<WritingListener> writingListeners);
	
	public void write() throws TrackWritingException;
}
