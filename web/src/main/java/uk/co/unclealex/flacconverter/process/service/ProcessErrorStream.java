package uk.co.unclealex.flacconverter.process.service;

public class ProcessErrorStream extends ProcessInputStream {

	public ProcessErrorStream(Process process) {
		super(process, process.getErrorStream());
	}
}
