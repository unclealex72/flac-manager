package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;

@Transactional
public interface EncoderService extends Serializable {

	public void encode(EncoderBean encoderBean, File flacFile,
			EncodingClosure closure, Map<EncoderBean, File> commandCache) throws IOException;

	public boolean encode(EncodingCommandBean encodingCommandBean, Map<EncoderBean, File> commandCache) throws IOException;

	public int encodeAll(int maximumThreads) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public int encodeAll() throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException;

	public int removeDeleted();

	public boolean isCurrentlyEncoding();

}