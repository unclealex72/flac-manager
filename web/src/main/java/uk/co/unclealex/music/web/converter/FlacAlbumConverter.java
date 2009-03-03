package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.dao.FlacAlbumDao;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

@Conversion
public class FlacAlbumConverter extends KeyedConverter<FlacAlbumBean> {

	private FlacAlbumDao i_flacAlbumDao;
	
	@Override
	protected KeyedReadOnlyDao<FlacAlbumBean> getDao() {
		return getFlacAlbumDao();
	}

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

}
