package uk.co.unclealex.music.encoder.service;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public class EncoderUrlPair implements Comparable<EncoderUrlPair>{

	public static final Transformer<EncodedTrackBean,EncoderUrlPair> ENCODED_TRACK_TRANSFORMER =
		new Transformer<EncodedTrackBean, EncoderUrlPair>() {
			@Override
			public EncoderUrlPair transform(EncodedTrackBean encodedTrackBean) {
				return new EncoderUrlPair(encodedTrackBean);
			}
	};


	public static Transformer<EncodingCommandBean, EncoderUrlPair> ENCODING_COMMAND_TRANSFORMER =
		new Transformer<EncodingCommandBean, EncoderUrlPair>() {
			@Override
			public EncoderUrlPair transform(EncodingCommandBean encodingCommandBean) {
				return new EncoderUrlPair(encodingCommandBean);
			}
		};
		
	private String i_encoder;
	private String i_url;
	
	public EncoderUrlPair(String encoder, String url) {
		i_encoder = encoder;
		i_url = url;
	}
	
	public EncoderUrlPair(EncodingCommandBean encodingCommandBean) {
		i_encoder = encodingCommandBean.getEncoderBean().getExtension();
		i_url = encodingCommandBean.getFlacTrackBean().getUrl();
	}
	
	public EncoderUrlPair(EncodedTrackBean encodedTrackBean) {
		i_encoder = encodedTrackBean.getEncoderBean().getExtension();
		i_url = encodedTrackBean.getFlacUrl();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof EncoderUrlPair) && compareTo((EncoderUrlPair) obj) == 0;
	}
	
	@Override
	public int compareTo(EncoderUrlPair o) {
		int cmp = getEncoder().compareTo(o.getEncoder());
		return cmp!=0?cmp:getUrl().compareTo(o.getUrl());
	}
	
	@Override
	public int hashCode() {
		return getUrl().hashCode() + getEncoder().hashCode();
	}
	
	@Override
	public String toString() {
		return getEncoder() + ";" + getUrl();
	}
	
	public String getEncoder() {
		return i_encoder;
	}
	public String getUrl() {
		return i_url;
	}
	
}
