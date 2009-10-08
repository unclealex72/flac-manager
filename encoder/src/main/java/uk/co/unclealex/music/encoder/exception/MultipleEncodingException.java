package uk.co.unclealex.music.encoder.exception;

import java.util.List;
import java.util.SortedMap;

import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.service.EncodingCommandBean;


public class MultipleEncodingException extends EncodingException {

	private SortedMap<EncodingCommandBean, Throwable> i_exceptionsByEncodingCommandBean;
	private List<EncodingAction> i_encodingActions;
	
	public MultipleEncodingException(
			SortedMap<EncodingCommandBean, Throwable> exceptionsByEncodingCommandBean, List<EncodingAction> encodingActions) {
		super();
		i_exceptionsByEncodingCommandBean = exceptionsByEncodingCommandBean;
		i_encodingActions = encodingActions;
	}

	public SortedMap<EncodingCommandBean, Throwable> getExceptionsByEncodingCommandBean() {
		return i_exceptionsByEncodingCommandBean;
	}

	public List<EncodingAction> getEncodingActions() {
		return i_encodingActions;
	}
}
