package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class HibernateFlacArtistDao extends HibernateCodeDao<FlacArtistBean> implements
		FlacArtistDao {

	@Override
	protected FlacArtistBean createBlankExample() {
		return new FlacArtistBean();
	}

}
