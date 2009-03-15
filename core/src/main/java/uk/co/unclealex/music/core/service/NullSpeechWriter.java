package uk.co.unclealex.music.core.service;

import java.io.File;

import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.service.SpeechWriter;

@Service
public class NullSpeechWriter implements SpeechWriter {

	@Override
	public void writeSpeechFiles(File baseDirectory, String extension) {
		// Do nothing
	}

}
