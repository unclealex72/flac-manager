package uk.co.unclealex.music.core.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface ProcessService {

	public ProcessResult run(ProcessBuilder processBuilder, boolean throwOnNonZeroReturn)
			throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, InputStream in, boolean throwOnNonZeroReturn)
	throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, Reader in, boolean throwOnNonZeroReturn)
	throws IOException;
}