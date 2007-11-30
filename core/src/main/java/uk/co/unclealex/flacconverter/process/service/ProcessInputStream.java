package uk.co.unclealex.flacconverter.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessInputStream extends InputStream {

	private Process i_process;
	private InputStream i_inputStream;
	private AtomicBoolean i_processFinished = new AtomicBoolean(false);
	private InterruptedException i_interruptedException;
	
	public ProcessInputStream(Process process) {
		this(process, process.getInputStream());
	}

	
	protected ProcessInputStream(Process process, InputStream inputStream) {
		super();
		i_process = process;
		i_inputStream = inputStream;
		new WaitForProcessThread().start();
	}

	@Override
	public int read() throws IOException {
		int read;
		while ((read = i_inputStream.read()) == -1 && !i_processFinished.get()) {
			// Wait for more input to arrive
		}
		if (i_interruptedException != null) {
			throw new IOException(i_interruptedException);
		}
		return read;
	}

	protected class WaitForProcessThread extends Thread {
		@Override
		public void run() {
			try {
				i_process.waitFor();
			}
			catch (InterruptedException e) {
				i_interruptedException = e;
			}
			i_processFinished.set(true);
		}
	}
}
