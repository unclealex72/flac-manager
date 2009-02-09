package uk.co.unclealex.music.core.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.process.service.ProcessResult;
import uk.co.unclealex.music.base.process.service.ProcessService;

@Service
public class ProcessServiceImpl implements ProcessService {

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.core.process.service.ProcessService#run(java.lang.ProcessBuilder)
	 */
	protected ProcessResult run(
			ProcessBuilder processBuilder, InputStream in, Reader reader, boolean throwOnNonZeroReturn) throws IOException {
		StringWriter errorWriter = new StringWriter();
		StringWriter outputWriter = new StringWriter();
		int returnValue;
		Process process = processBuilder.start();
		try {
			OutputStream stdin = process.getOutputStream();
			if (in != null) {
				IOUtils.copy(in, stdin);
			}
			if (reader != null) {
				IOUtils.copy(reader, stdin);
			}
			returnValue = process.waitFor();
		}
		catch (InterruptedException e) {
			throw new IOException(
					"The process " + StringUtils.join(processBuilder.command().iterator(), ' ') + " was interrupted", e);
		}
		catch(IOException e) {
			StringWriter writer = new StringWriter();
			IOUtils.copy(process.getErrorStream(), writer);
			throw new IOException(
					"The process " + StringUtils.join(processBuilder.command().iterator(), ' ') +" failed.\n" + 
					writer, e);
		}
		IOUtils.copy(process.getInputStream(), outputWriter);
		IOUtils.copy(process.getErrorStream(), errorWriter);
		if (returnValue != 0 && throwOnNonZeroReturn) {
			throw new IOException(
					"The process " + StringUtils.join(processBuilder.command().iterator(), ' ') +
					" failed with return value " + returnValue + ":\n" + errorWriter);
		}
		return new ProcessResult(returnValue, outputWriter.toString(), errorWriter.toString());
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, null, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, InputStream in,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, in, null, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, Reader in,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, in, throwOnNonZeroReturn);
	}
}
