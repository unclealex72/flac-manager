package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public class HibernateEncoderDao extends HibernateEncodedDao<EncoderBean>
		implements EncoderDao {

	@Override
	public EncoderBean createExampleBean() {
		return new EncoderBean();
	}
}
