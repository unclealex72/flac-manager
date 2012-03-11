package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.io.Serializable;

import uk.co.unclealex.music.legacy.Encoding;

public interface SingleEncodingService extends Serializable {

	public void encode(Encoding encoding, File flacFile, File encodingScript, File encodedDestination) throws EncodingException;
}