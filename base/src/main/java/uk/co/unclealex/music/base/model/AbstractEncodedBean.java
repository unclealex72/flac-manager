package uk.co.unclealex.music.base.model;

import uk.co.unclealex.hibernate.model.KeyedBean;

public abstract class AbstractEncodedBean<T extends AbstractEncodedBean<T>> extends KeyedBean<T> implements EncodedBean {

	private String i_filename;

	public String getFilename() {
		return i_filename;
	}

	public void setFilename(String filename) {
		i_filename = filename;
	}
}
