package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.Serializable;

public interface SingleEncodingService extends Serializable {

	public void encode(Encoding encoding, File flacFile, File encodingScript, File encodedDestination) throws EncodingException;
}