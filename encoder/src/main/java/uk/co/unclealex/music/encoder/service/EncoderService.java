package uk.co.unclealex.music.encoder.service;

import java.util.List;

import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;


public interface EncoderService {

	public List<EncodingAction> encodeAll(int maximumThreads) throws EncodingException;

	public List<EncodingAction> encodeAll() throws EncodingException;
}
