package uk.co.unclealex.music.encoder.service;

import java.io.IOException;

import uk.co.unclealex.music.core.io.KnownLengthInputStream;

public interface EncodingClosure {

	public void process(KnownLengthInputStream in) throws IOException;
}
