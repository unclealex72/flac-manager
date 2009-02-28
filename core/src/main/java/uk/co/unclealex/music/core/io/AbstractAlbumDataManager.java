package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.KeyedDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

@Transactional(rollbackFor=IOException.class)
public abstract class AbstractAlbumDataManager extends AbstractDataManager<AlbumCoverBean> {

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	protected KeyedDao<AlbumCoverBean> getDao() {
		return getAlbumCoverDao();
	}
	
	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}
	
	@Required
	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}
}
