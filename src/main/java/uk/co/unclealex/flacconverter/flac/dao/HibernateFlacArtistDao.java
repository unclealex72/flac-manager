package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class HibernateFlacArtistDao extends HibernateCodeDao<FlacArtistBean> implements
		FlacArtistDao {

	@Override
	public FlacArtistBean createExampleBean() {
		return new FlacArtistBean();
	}

}
