package uk.co.unclealex.music.core.io;

import java.io.ByteArrayInputStream;

public class KnownLengthByteArrayInputStream extends KnownLengthInputStream {

	public KnownLengthByteArrayInputStream(byte[] data) {
		super(new ByteArrayInputStream(data), data.length);
	}
}
