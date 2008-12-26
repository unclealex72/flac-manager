package uk.co.unclealex.music.core.model;


public abstract class AbstractFlacBean<T extends AbstractFlacBean<T>> extends KeyedBean<T> implements FlacBean {

	private String i_code;
	
	@Override
	public abstract String toString();
	
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
