package uk.co.unclealex.music.encoder.service;

import java.io.IOException;


public interface EncoderService {

	public void registerEncodingEventListener(EncodingEventListener encodingEventListener);
	
	public EncoderResultBean encodeAll(int maximumThreads) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public EncoderResultBean encodeAll() throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public EncoderResultBean encodeAllAndRemoveDeleted() throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public boolean isCurrentlyEncoding();
}
