package uk.co.unclealex.music.encoder.service;

import org.apache.commons.lang.ObjectUtils;

import uk.co.unclealex.music.base.service.CommandBean;

public class EncodingCommandBean extends CommandBean<EncodingCommandBean> {

	private String i_extension;
	private String i_url;
	
	public EncodingCommandBean() {
		super();
	}
	
	public EncodingCommandBean(String extension, String url) {
		super();
		i_extension = extension;
		i_url = url;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getUrl()) + 3 * ObjectUtils.hashCode(getExtension());
	}
	
	public int compareTo(EncodingCommandBean o) {
		int cmp = getExtension().compareTo(o.getExtension());
		return cmp==0?getUrl().compareTo(o.getUrl()):cmp;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EncodingCommandBean && compareTo((EncodingCommandBean) obj) == 0;
	}
	
	@Override
	public boolean isEndOfWorkBean() {
		return getExtension() == null && getUrl() == null;
	}
	
	@Override
	public String toString() {
		return "<" + getExtension() + "," + getUrl() + ">";
	}

	public String getExtension() {
		return i_extension;
	}

	public String getUrl() {
		return i_url;
	}
}
