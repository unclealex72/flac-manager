package uk.co.unclealex.music.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class CountingWritableByteChannel implements WritableByteChannel {

	private WritableByteChannel i_channel;
	private long i_count;
	
	public CountingWritableByteChannel(WritableByteChannel channel) {
		super();
		i_channel = channel;
	}

	public void close() throws IOException {
		getChannel().close();
	}

	public boolean isOpen() {
		return getChannel().isOpen();
	}

	public int write(ByteBuffer src) throws IOException {
		int count = getChannel().write(src);
		setCount(getCount() + count);
		return count;
	}

	public WritableByteChannel getChannel() {
		return i_channel;
	}
	
	public void setCount(long count) {
		i_count = count;
	}
	
	public long getCount() {
		return i_count;
	}
	
}
