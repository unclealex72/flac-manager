package uk.co.unclealex.music.core.process.service;

public class ProcessErrorStream extends ProcessInputStream {

	public ProcessErrorStream(Process process) {
		super(process, process.getErrorStream());
	}
}
