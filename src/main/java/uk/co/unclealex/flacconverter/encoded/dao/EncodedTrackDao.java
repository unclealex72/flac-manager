package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public interface EncodedTrackDao extends EncodedDao<EncodedTrackBean> {

	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean);

	public SortedSet<EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean);
}
