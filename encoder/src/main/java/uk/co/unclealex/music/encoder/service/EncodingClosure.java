package uk.co.unclealex.music.encoder.service;

import java.io.IOException;
import java.io.InputStream;

public interface EncodingClosure {

	public void process(InputStream in) throws IOException;
}
