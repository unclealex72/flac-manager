package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncodedArtistBean;

@Conversion
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
