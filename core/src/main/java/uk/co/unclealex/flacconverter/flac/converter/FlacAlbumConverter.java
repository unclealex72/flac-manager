package uk.co.unclealex.flacconverter.flac.converter;

import uk.co.unclealex.flacconverter.converter.KeyedConverter;
import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacAlbumDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;

public class FlacAlbumConverter extends KeyedConverter<FlacAlbumBean> {

	private FlacAlbumDao i_flacAlbumDao;
	
	@Override
	protected KeyedDao<FlacAlbumBean> getDao() {
		return getFlacAlbumDao();
	}

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}
}
