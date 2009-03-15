package uk.co.unclealex.music.base.io;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.input.ProxyInputStream;

public class KnownLengthInputStream extends ProxyInputStream {

	private int i_length;
	private ReadableByteChannel i_channel;
	
	public KnownLengthInputStream(InputStream in, ReadableByteChannel channel, int length) {
		super(in);
		i_length = length;
		i_channel = channel;
	}

	public boolean hasChannel() {
		return getChannel() != null;
	}
	
	public ReadableByteChannel getChannel() {
		return i_channel;
	}
	
	public int getLength() {
		return i_length;
	}
}
