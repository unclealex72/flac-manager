package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;

@Conversion
public class EncodedAlbumConverter extends KeyedConverter<EncodedAlbumBean> {

	private EncodedAlbumDao i_encodedAlbumDao;
	
	@Override
	protected KeyedReadOnlyDao<EncodedAlbumBean> getDao() {
		return getEncodedAlbumDao();
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

}
