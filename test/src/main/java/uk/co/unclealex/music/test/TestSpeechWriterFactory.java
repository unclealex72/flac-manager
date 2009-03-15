package uk.co.unclealex.music.test;

import java.io.File;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.service.SpeechWriter;
import uk.co.unclealex.music.base.service.SpeechWriterFactory;

public class TestSpeechWriterFactory implements SpeechWriterFactory, SpeechWriter {

	@Override
	public SpeechWriter createSpeechWriter(DeviceBean deviceBean) {
		return this;
	}

	@Override
	public void writeSpeechFiles(File baseDirectory, String extension) {
		// Do nothing
	}

}
