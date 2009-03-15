package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.io.output.TeeOutputStream;

import uk.co.unclealex.music.base.io.KnownLengthOutputStream;

public class TeeKnownLengthOutputStream extends KnownLengthOutputStream {

	private KnownLengthOutputStream i_left;
	private KnownLengthOutputStream i_right;
	private TeeOutputStream i_teeOutputStream;
	
	public TeeKnownLengthOutputStream(KnownLengthOutputStream left, KnownLengthOutputStream right) {
		i_left = left;
		i_right = right;
		i_teeOutputStream = new TeeOutputStream(left, right);
	}

	@Override
	public boolean hasChannel() {
		return getLeft().hasChannel() && getRight().hasChannel();
	}
	
	@Override
	public WritableByteChannel getChannel() {
		if (!hasChannel()) {
			return null;
		}
		final WritableByteChannel leftChannel = getLeft().getChannel();
		final WritableByteChannel rightChannel = getRight().getChannel();
		return new WritableByteChannel() {

			@Override
			public int write(ByteBuffer src) throws IOException {
				src.mark();
				leftChannel.write(src);
				src.reset();
				return rightChannel.write(src);
			}

			@Override
			public void close() throws IOException {
				leftChannel.close();
				rightChannel.close();
			}

			@Override
			public boolean isOpen() {
				return leftChannel.isOpen() && rightChannel.isOpen();
			}
			
		};
	}
	
	@Override
	public void setLength(int length) throws IOException {
		getLeft().setLength(length);
		getRight().setLength(length);
	}

	public void close() throws IOException {
		getTeeOutputStream().close();
	}

	public boolean equals(Object obj) {
		return getTeeOutputStream().equals(obj);
	}

	public void flush() throws IOException {
		getTeeOutputStream().flush();
	}

	public int hashCode() {
		return getTeeOutputStream().hashCode();
	}

	public String toString() {
		return getTeeOutputStream().toString();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		getTeeOutputStream().write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		getTeeOutputStream().write(b);
	}

	public void write(int b) throws IOException {
		getTeeOutputStream().write(b);
	}

	public KnownLengthOutputStream getLeft() {
		return i_left;
	}

	public KnownLengthOutputStream getRight() {
		return i_right;
	}

	public TeeOutputStream getTeeOutputStream() {
		return i_teeOutputStream;
	}
}
