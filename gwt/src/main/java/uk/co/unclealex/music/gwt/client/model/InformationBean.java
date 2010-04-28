package uk.co.unclealex.music.gwt.client.model;

import java.io.Serializable;

public abstract class InformationBean<I extends InformationBean<I>> implements Serializable, Comparable<I> {

	private String i_url;
	
	public InformationBean() {
		super();
	}

	public InformationBean(String url) {
		super();
		i_url = url;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass()) && compareTo((I) obj) == 0;
	}
	
	public int compareTo(I o) {
		return getUrl().compareTo(o.getUrl());
	}
	
	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}
	
	public String getUrl() {
		return i_url;
	}

	public void setUrl(String url) {
		i_url = url;
	}
}
