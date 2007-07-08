package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;

@Transactional
public interface EncoderService {

	public void encode(EncoderBean encoderBean, File flacFile,
			EncodingClosure closure) throws IOException;

	public boolean encode(EncodingCommandBean encodingCommandBean) throws IOException;

	public int encodeAll(int maximumThreads) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException;

	public int removeDeleted();

	public boolean isCurrentlyEncoding();

}