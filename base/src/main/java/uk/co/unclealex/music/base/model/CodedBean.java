package uk.co.unclealex.music.base.model;

public abstract class CodedBean<T extends CodedBean<T>>
		extends AbstractEncodedBean<T> implements Coded<String> {

	private String i_code;

	public int compareTo(T coded) {
		int cmp = getCode().compareTo(coded.getCode());
		return cmp==0?super.compareTo(coded):cmp;
	};
	
	public String getCode() {
		return i_code;
	}

	public void setCode(String code) {
		i_code = code;
	}
	
}
