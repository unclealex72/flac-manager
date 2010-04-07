package uk.co.unclealex.music;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import uk.co.unclealex.music.ProcessCallback;

public interface ProcessService {

	public ProcessResult run(ProcessBuilder processBuilder, boolean throwOnNonZeroReturn)
			throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, InputStream in, boolean throwOnNonZeroReturn)
	throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, Reader in, boolean throwOnNonZeroReturn)
	throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, ProcessCallback processCallback, boolean throwOnNonZeroReturn)
			throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, InputStream in, ProcessCallback processCallback,
			boolean throwOnNonZeroReturn) throws IOException;

	public ProcessResult run(ProcessBuilder processBuilder, Reader in, ProcessCallback processCallback,
			boolean throwOnNonZeroReturn) throws IOException;
}