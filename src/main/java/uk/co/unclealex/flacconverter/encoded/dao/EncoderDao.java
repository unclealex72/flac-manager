package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public interface EncoderDao extends EncodingDao<EncoderBean> {

	public EncoderBean findByExtension(String extension);

}
