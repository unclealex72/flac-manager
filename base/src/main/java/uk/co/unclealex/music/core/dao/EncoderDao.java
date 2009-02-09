package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.EncoderBean;

public interface EncoderDao extends KeyedDao<EncoderBean> {

	public EncoderBean findByExtension(String extension);

}
