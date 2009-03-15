package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.music.base.process.service.ProcessService;
import uk.co.unclealex.spring.Prototype;

@Prototype
public class RockboxSpeechWriter extends AbstractSpeechWriter {
	
	private ProcessService i_processService;
	
	@Override
	protected File createSpeechFile(File file) throws IOException {
		File speechFile;
		if (file.isDirectory()) {
			speechFile = new File(file, "_dirname.talk");
		}
		else {
			speechFile = new File(file.getPath() + ".talk");
		}
		return speechFile; 
	}

	@Override
	protected void writeSpeechFile(File actualFile, String name, File speechFile) throws IOException {
		ProcessService processService = getProcessService();
		File wavFile = File.createTempFile("speech", null);
		try {
			String wavPath = wavFile.getCanonicalPath();
			ProcessBuilder espeakProcessBuilder = 
				new ProcessBuilder("espeak", "-w", wavPath, name);
			processService.run(espeakProcessBuilder, true);
			ProcessBuilder speexProcessBuilder = new ProcessBuilder("rbspeexenc", wavPath, speechFile.getCanonicalPath());
			processService.run(speexProcessBuilder, true);
		}
		finally {
			wavFile.delete();
		}
	}

	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

}
