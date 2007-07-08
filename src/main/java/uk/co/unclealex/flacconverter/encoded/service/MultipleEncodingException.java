package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedMap;

import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;

public class MultipleEncodingException extends EncodingException {

	private SortedMap<EncodingCommandBean, Throwable> i_exceptionsByEncodingCommandBean;
	private int i_totalEncodedSuccessfully;
	
	public MultipleEncodingException(
			SortedMap<EncodingCommandBean, Throwable> exceptionsByEncodingCommandBean, int totalEncodedSuccessfully) {
		super();
		i_exceptionsByEncodingCommandBean = exceptionsByEncodingCommandBean;
		i_totalEncodedSuccessfully = totalEncodedSuccessfully;
	}

	public SortedMap<EncodingCommandBean, Throwable> getExceptionsByEncodingCommandBean() {
		return i_exceptionsByEncodingCommandBean;
	}

	protected void setExceptionsByEncodingCommandBean(
			SortedMap<EncodingCommandBean, Throwable> exceptionsByEncodingCommandBean) {
		i_exceptionsByEncodingCommandBean = exceptionsByEncodingCommandBean;
	}

	public int getTotalEncodedSuccessfully() {
		return i_totalEncodedSuccessfully;
	}

	protected void setTotalEncodedSuccessfully(int totalEncodedSuccessfully) {
		i_totalEncodedSuccessfully = totalEncodedSuccessfully;
	}
}
