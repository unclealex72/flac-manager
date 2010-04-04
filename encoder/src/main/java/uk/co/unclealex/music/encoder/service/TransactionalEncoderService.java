package uk.co.unclealex.music.encoder.service;

import java.io.Serializable;
import java.util.List;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.exception.EventException;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;

public interface TransactionalEncoderService extends Serializable {

	public void encode(
			FlacTrackBean flacTrackBean, EncoderBean encoderBean, List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EncodingException;
	
	public void remove(
			EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EncodingException;

	public void startEncoding(List<EncodingEventListener> encodingEventListeners);

	public void stopEncoding(List<EncodingEventListener> encodingEventListeners);

	public void own(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions,
			List<EncodingEventListener> encodingEventListeners) throws EventException;

	public void unown(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions,
			List<EncodingEventListener> encodingEventListeners) throws EventException;
}