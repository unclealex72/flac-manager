package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncodedArtistBean;

public class EncodedArtistConverter extends KeyedConverter<EncodedArtistBean> {

	private EncodedArtistDao i_encodedArtistDao;
	
	@Override
	protected KeyedReadOnlyDao<EncodedArtistBean> getDao() {
		return getEncodedArtistDao();
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

}
