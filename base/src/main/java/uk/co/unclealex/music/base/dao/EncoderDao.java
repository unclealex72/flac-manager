package uk.co.unclealex.music.base.dao;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.EncoderBean;

public interface EncoderDao extends KeyedDao<EncoderBean> {

	public EncoderBean findByExtension(String extension);

}
