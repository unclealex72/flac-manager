package uk.co.unclealex.music.base.io;

import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

public class KnownLengthInputStream extends ProxyInputStream {

	private int i_length;
	
	public KnownLengthInputStream(InputStream in, int length) {
		super(in);
		i_length = length;
	}

	public int getLength() {
		return i_length;
	}
}
