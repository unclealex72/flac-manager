package uk.co.unclealex.flacconverter.flac.converter;

import uk.co.unclealex.flacconverter.converter.KeyedConverter;
import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacArtistDao;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class FlacArtistConverter extends KeyedConverter<FlacArtistBean> {

	private FlacArtistDao i_flacArtistDao;
	
	@Override
	protected KeyedDao<FlacArtistBean> getDao() {
		return getFlacArtistDao();
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}
}
