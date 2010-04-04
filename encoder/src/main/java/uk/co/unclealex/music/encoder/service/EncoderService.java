package uk.co.unclealex.music.encoder.service;

import java.util.List;

import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;


public interface EncoderService {

	public List<EncodingAction> encodeAll(List<EncodingEventListener> encodingEventListeners) throws EncodingException;

	public List<EncodingAction> encodeAll() throws EncodingException;
}
