package uk.co.unclealex.music.base.service;

import java.io.File;
import java.util.Collection;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.base.writer.WritingListener;

public interface DevicesWriter {

	public void addDevice(DeviceBean deviceBean, File deviceDirectory, Collection<WritingListener> writingListeners);
	
	public void write(boolean unmountAfterWriting) throws TrackWritingException;
}
