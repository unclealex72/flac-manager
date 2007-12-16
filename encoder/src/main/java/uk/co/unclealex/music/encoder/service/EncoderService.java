package uk.co.unclealex.music.encoder.service;

import java.io.IOException;

public interface EncoderService {

	public void registerEncodingEventListener(EncodingEventListener encodingEventListener);
	
	public int encodeAll(int maximumThreads) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public int encodeAll() throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public int removeDeleted();

	public int encodeAllAndRemoveDeleted() throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public boolean isCurrentlyEncoding();

}
