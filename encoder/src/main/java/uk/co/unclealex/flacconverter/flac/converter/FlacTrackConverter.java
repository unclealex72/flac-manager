package uk.co.unclealex.flacconverter.flac.converter;

import uk.co.unclealex.flacconverter.converter.KeyedConverter;
import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class FlacTrackConverter extends KeyedConverter<FlacTrackBean> {

	private FlacTrackDao i_flacTrackDao;
	
	@Override
	protected KeyedDao<FlacTrackBean> getDao() {
		return getFlacTrackDao();
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
