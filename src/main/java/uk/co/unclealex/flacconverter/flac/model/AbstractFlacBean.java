package uk.co.unclealex.flacconverter.flac.model;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;
import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;

public class AbstractFlacBean<T extends AbstractFlacBean<T>> extends KeyedBean<T> implements FlacBean {

	private String i_code;

	@Override
	public void accept(FlacVisitor flacVisitor) {
		flacVisitor.visit(this);
	}
	
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
