package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.util.Collection;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.writer.TrackWritingException;
import uk.co.unclealex.flacconverter.encoded.writer.WritingListener;

public interface DevicesWriter {

	public void addDevice(DeviceBean deviceBean, File deviceDirectory, Collection<WritingListener> writingListeners);
	
	public void write() throws TrackWritingException;
}
