package uk.co.unclealex.flacconverter.encoded.model;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class EncodingCommandBean implements Comparable<EncodingCommandBean> {

	private EncoderBean i_encoderBean;
	private FlacTrackBean i_flacTrackBean;
	
	public EncodingCommandBean() {
		super();
	}
	
	public EncodingCommandBean(EncoderBean encoderBean, FlacTrackBean flacTrackBean) {
		super();
		i_encoderBean = encoderBean;
		i_flacTrackBean = flacTrackBean;
	}

	@Override
	public int hashCode() {
		return getEncoderBean().hashCode();
	}
	
	public int compareTo(EncodingCommandBean o) {
		int cmp = getEncoderBean().compareTo(o.getEncoderBean());
		return cmp==0?getFlacTrackBean().compareTo(o.getFlacTrackBean()):cmp;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EncodingCommandBean && compareTo((EncodingCommandBean) obj) == 0;
	}
	
	@Override
	public String toString() {
		return "<" + getEncoderBean() + "," + getFlacTrackBean() + ">";
	}
	
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}
	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}
	public FlacTrackBean getFlacTrackBean() {
		return i_flacTrackBean;
	}
	public void setFlacTrackBean(FlacTrackBean flacTrackBean) {
		i_flacTrackBean = flacTrackBean;
	}
}
