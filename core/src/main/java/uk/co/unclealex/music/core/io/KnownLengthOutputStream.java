package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class KnownLengthOutputStream<O extends OutputStream> extends OutputStream {

	private O i_out;
	
	public KnownLengthOutputStream(O out) {
		i_out = out;
	}

	@Override
	public void write(int b) throws IOException {
		getOut().write(b);
	}

	protected abstract void setLength(int length) throws IOException;
	
	public O getOut() {
		return i_out;
	}
}
