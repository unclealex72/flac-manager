package uk.co.unclealex.music.core.service;

import java.io.File;

import uk.co.unclealex.music.base.service.SpeechWriter;

public class NullSpeechWriter implements SpeechWriter {

	@Override
	public void writeSpeechFiles(File baseDirectory, String extension) {
		// Do nothing
	}

}
