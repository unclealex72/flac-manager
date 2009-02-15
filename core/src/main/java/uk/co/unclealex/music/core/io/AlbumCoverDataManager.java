package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.KeyedDao;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

@Service
@Transactional
public class AlbumCoverDataManager extends AbstractDataManager<AlbumCoverBean> {

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	public void extractData(int id,
			KnownLengthInputStreamCallback callback) throws IOException {
		getAlbumCoverDao().streamCover(id, callback);
	}

	@Override
	protected void doInjectData(AlbumCoverBean albumCoverBean, KnownLengthInputStream data)
			throws IOException {
		albumCoverBean.setCover(data);
	}
	
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
