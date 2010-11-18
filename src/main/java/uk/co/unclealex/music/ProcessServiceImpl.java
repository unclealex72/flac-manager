package uk.co.unclealex.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class ProcessServiceImpl implements ProcessService {

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.core.process.service.ProcessService#run(java.lang.ProcessBuilder)
	 */
	protected ProcessResult run(
			ProcessBuilder processBuilder, InputStream in, Reader reader, ProcessCallback processCallback, boolean throwOnNonZeroReturn) throws IOException {
		int returnValue;
		InputStream stdout = null;
		OutputStream stdin = null;
		InputStream stderr = null;
		try {
			final ProcessCallback actualProcessCallback = 
				processCallback!=null?
						processCallback:
						new ProcessCallback() {
							@Override
							public void lineWritten(String line) {
							}
							@Override
							public void errorLineWritten(String line) {
							}
						};
			Process process = processBuilder.start();
			stdout = process.getInputStream();
			OutputWriter stdOutOutputWriter = new OutputWriter(stdout) {
				
				@Override
				protected void writeLine(String line) {
					actualProcessCallback.lineWritten(line);
				}
			};
			stderr = process.getErrorStream();
			OutputWriter stdErrOutputWriter = new OutputWriter(stderr) {
				
				@Override
				protected void writeLine(String line) {
					actualProcessCallback.errorLineWritten(line);
				}
			};
			ExecutorService executorService = Executors.newFixedThreadPool(2);
			Future<String> stdOutFuture = executorService.submit(stdOutOutputWriter);
			Future<String> stdErrFuture = executorService.submit(stdErrOutputWriter);
			try {
				stdin = process.getOutputStream();
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
				IOUtils.copy(stderr, writer);
				throw new IOException(
						"The process " + StringUtils.join(processBuilder.command().iterator(), ' ') +" failed.\n" + 
						writer, e);
			}
			finally {
				stdOutOutputWriter.setKeepRunning(false);
				stdErrOutputWriter.setKeepRunning(false);
			}
			try {
				String output;
				String error;
				try {
					output = stdOutFuture.get(1, TimeUnit.SECONDS);
					error = stdErrFuture.get(1, TimeUnit.SECONDS);
				}
				catch (TimeoutException e) {
					throw new IOException(
							"Reading from the process error streams failed for process " + StringUtils.join(processBuilder.command().iterator(), ' '));
				}
				if (returnValue != 0 && throwOnNonZeroReturn) {
					throw new IOException(
							"The process " + StringUtils.join(processBuilder.command().iterator(), ' ') +
							" failed with return value " + returnValue + ":\n" + error);
				}
				return new ProcessResult(returnValue, output, error);
			}
			catch (ExecutionException e) {
				throw new IOException("An unexpected error occurred.", e);
			}
			catch (InterruptedException e) {
				throw new IOException("An unexpected error occurred.", e);
			}
		}
		finally {
			IOUtils.closeQuietly(stdin);
			IOUtils.closeQuietly(stdout);
			IOUtils.closeQuietly(stderr);
		}
	}

	protected abstract class OutputWriter implements Callable<String> {
	
		private InputStream i_in;
		private boolean i_keepRunning;
		
		public OutputWriter(InputStream in) {
			super();
			i_in = in;
			i_keepRunning = true;
		}

		@Override
		public String call() throws IOException {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(getIn()));
			String line;
			while (isKeepRunning() && (line = reader.readLine()) != null) {
				builder.append(line).append('\n');
				writeLine(line);
			}
			return builder.toString();
		}

		protected abstract void writeLine(String line);
		
		public InputStream getIn() {
			return i_in;
		}

		public boolean isKeepRunning() {
			return i_keepRunning;
		}

		public void setKeepRunning(boolean keepRunning) {
			i_keepRunning = keepRunning;
		}
	}
	
	@Override
	public ProcessResult run(ProcessBuilder processBuilder,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, null, null, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, InputStream in,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, in, null, null, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, Reader in,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, in, null, throwOnNonZeroReturn);
	}
	
	@Override
	public ProcessResult run(ProcessBuilder processBuilder, ProcessCallback processCallback,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, null, processCallback, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, InputStream in, ProcessCallback processCallback,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, in, null, processCallback, throwOnNonZeroReturn);
	}

	@Override
	public ProcessResult run(ProcessBuilder processBuilder, Reader in, ProcessCallback processCallback,
			boolean throwOnNonZeroReturn) throws IOException {
		return run(processBuilder, null, in, processCallback, throwOnNonZeroReturn);
	}

}
