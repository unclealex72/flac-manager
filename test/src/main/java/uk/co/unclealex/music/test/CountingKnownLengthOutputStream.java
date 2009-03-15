package uk.co.unclealex.music.test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.io.output.CountingOutputStream;

import uk.co.unclealex.music.base.io.KnownLengthOutputStream;

public class CountingKnownLengthOutputStream extends KnownLengthOutputStream {

	public CountingKnownLengthOutputStream(OutputStream out, WritableByteChannel channel) {
		super(new CountingOutputStream(out), new CountingWritableByteChannel(channel));
	}
	
	@Override
	public void setLength(int length) throws IOException {
		// Do nothing
	}

	public long getCount() {
		return ((CountingOutputStream) getOut()).getByteCount() + ((CountingWritableByteChannel) getChannel()).getCount();
	}
}
