package uk.co.unclealex.music.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class NullWritableByteChannel implements WritableByteChannel {

	private boolean i_open = true;
	
	@Override
	public int write(ByteBuffer src) throws IOException {
		int size = src.remaining();
		while (src.remaining() != 0) {
			src.get();
		}
		return size;
	}

	@Override
	public void close() throws IOException {
		setOpen(true);
	}

	public boolean isOpen() {
		return i_open;
	}

	public void setOpen(boolean open) {
		i_open = open;
	}

}
