package uk.co.unclealex.music.encoder.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.Lock;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;

public interface TransactionalEncoderService extends Serializable {

	public void encode(
			FlacTrackBean flacTrackBean, EncoderBean encoderBean, List<EncodingAction> encodingActions, Lock lock) throws EncodingException;
	
	public void remove(
			EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions, Lock lock) throws EncodingException;

	public void updateOwnership(List<EncodingAction> encodingActions) throws EncodingException;

	public void startEncoding();

	public void stopEncoding();
}