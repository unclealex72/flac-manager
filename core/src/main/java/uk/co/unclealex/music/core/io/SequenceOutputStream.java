package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class SequenceOutputStream extends OutputStream {

	private Iterator<OutputStream> outputStreamIterator;
	private OutputStream currentOutputStream;
	private int threshold;
	private int available;
	private long bytesWritten = 0;
	
	public SequenceOutputStream(int threshold,
			Iterator<OutputStream> outputStreamIterator) {
		this.threshold = threshold;
		this.outputStreamIterator = outputStreamIterator;
		available = threshold;
	}

	
	@Override
	public void close() throws IOException {
    try
    {
        flush();
    }
    catch (IOException ignored)
    {
        // ignore
    }
    getStream().close();
	}


	@Override
	public void flush() throws IOException {
		super.flush();
	}


	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		while (len != 0) {
			if (available == 0) {
				thresholdReached();
				available = threshold;
			}
			int writeNow = Math.min(len, available);
			getStream().write(b, off, writeNow);
			off += writeNow;
			len -= writeNow;
			bytesWritten += writeNow;
			available -= writeNow;
		}
	}


	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}


	@Override
	public void write(int b) throws IOException {
		byte[] by = new byte[1];
		by[0] = (byte) b;
		write(by);
	}

	protected OutputStream getStream() throws IOException {
		if (currentOutputStream == null) {
			updateIterator();
		}
		return currentOutputStream;
	}

	protected void thresholdReached() throws IOException {
		updateIterator();
	}
	
	protected void updateIterator() throws IOException {
		if (currentOutputStream != null) {
			currentOutputStream.close();
		}
		if (outputStreamIterator.hasNext()) {
			currentOutputStream = outputStreamIterator.next();
		}
		else {
			throw new IOException("Could not create a new output stream.");
		}
	}

	public int getThreshold() {
		return threshold;
	}

	public long getBytesWritten() {
		return bytesWritten;
	}
}
