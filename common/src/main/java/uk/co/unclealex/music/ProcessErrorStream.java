package uk.co.unclealex.music;

public class ProcessErrorStream extends ProcessInputStream {

	public ProcessErrorStream(Process process) {
		super(process, process.getErrorStream());
	}
}
