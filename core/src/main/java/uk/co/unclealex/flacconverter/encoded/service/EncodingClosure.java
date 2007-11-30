package uk.co.unclealex.flacconverter.encoded.service;

import java.io.IOException;
import java.io.InputStream;

public interface EncodingClosure {

	public void process(InputStream in) throws IOException;
}
