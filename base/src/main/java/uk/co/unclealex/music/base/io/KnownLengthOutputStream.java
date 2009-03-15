package uk.co.unclealex.music.base.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

public abstract class KnownLengthOutputStream extends OutputStream {

	private OutputStream i_out;
	private WritableByteChannel i_channel;
	
	public KnownLengthOutputStream() {
		// Default constructor
	}
	
	public KnownLengthOutputStream(OutputStream out, WritableByteChannel channel) {
		i_out = out;
		i_channel = channel;
	}

	public boolean hasChannel() {
		return getChannel() != null;
	}
	
	public WritableByteChannel getChannel() {
		return i_channel;
	}

	public abstract void setLength(int length) throws IOException;
	
	public void close() throws IOException {
		getOut().close();
	}

	public void flush() throws IOException {
		getOut().flush();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		getOut().write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		getOut().write(b);
	}

	public void write(int b) throws IOException {
		getOut().write(b);
	}

	public OutputStream getOut() {
		return i_out;
	}

}
