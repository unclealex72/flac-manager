package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.io.output.ThresholdingOutputStream;

public class SequenceOutputStream extends ThresholdingOutputStream {

	private Iterator<OutputStream> i_outputStreamIterator;
	private OutputStream i_currentOutputStream;
	
	public SequenceOutputStream(int threshold,
			Iterator<OutputStream> outputStreamIterator) {
		super(threshold);
		i_outputStreamIterator = outputStreamIterator;
	}

	@Override
	protected OutputStream getStream() throws IOException {
		if (i_currentOutputStream == null) {
			updateIterator();
		}
		return i_currentOutputStream;
	}

	@Override
	protected void thresholdReached() throws IOException {
		updateIterator();
		i_currentOutputStream = new SequenceOutputStream(getThreshold(), i_outputStreamIterator);
	}
	
	protected void updateIterator() throws IOException {
		if (i_currentOutputStream != null) {
			i_currentOutputStream.close();
		}
		if (i_outputStreamIterator.hasNext()) {
			i_currentOutputStream = i_outputStreamIterator.next();
		}
		else {
			throw new IOException("Could not create a new output stream.");
		}
	}

}
