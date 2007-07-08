package uk.co.unclealex.flacconverter.flac.model;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

public class CodedBean<T extends CodedBean<T>> extends KeyedBean<T> {

	private String i_code;

	@Override
	public int compareTo(T o) {
		return getCode().compareTo(o.getCode());
	}
	
	public String getCode() {
		return i_code;
	}

	public void setCode(String code) {
		i_code = code;
	}
}
