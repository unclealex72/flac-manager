package uk.co.unclealex.music.encoder.encoded.service.titleformat;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.encoder.model.FlacBean;

public class TitleFormatMatch {

	private SortedSet<FlacBean> i_matchedFlacBeans = new TreeSet<FlacBean>();
	private SortedSet<EncoderBean> i_encoderBean = new TreeSet<EncoderBean>();
	
	
	public TitleFormatMatch() {
		super();
	}
	
	public TitleFormatMatch(SortedSet<FlacBean> matchedFlacBeans,
			SortedSet<EncoderBean> encoderBean) {
		super();
		i_matchedFlacBeans = matchedFlacBeans;
		i_encoderBean = encoderBean;
	}
	
	public SortedSet<FlacBean> getMatchedFlacBeans() {
		return i_matchedFlacBeans;
	}
	
	public void setMatchedFlacBeans(SortedSet<FlacBean> matchedFlacBeans) {
		i_matchedFlacBeans = matchedFlacBeans;
	}
	
	public SortedSet<EncoderBean> getEncoderBean() {
		return i_encoderBean;
	}
	
	public void setEncoderBean(SortedSet<EncoderBean> encoderBean) {
		i_encoderBean = encoderBean;
	}
	
}
