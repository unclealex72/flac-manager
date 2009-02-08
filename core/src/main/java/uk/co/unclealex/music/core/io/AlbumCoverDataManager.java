package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.AlbumCoverDao;
import uk.co.unclealex.music.core.model.AlbumCoverBean;

@Service
@Transactional
public class AlbumCoverDataManager implements DataManager<AlbumCoverBean> {

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	public void extractData(int id,
			KnownLengthInputStreamCallback callback) throws IOException {
		getAlbumCoverDao().streamCover(id, callback);
	}

	@Override
	public void injectData(AlbumCoverBean albumCoverBean, KnownLengthInputStream data)
			throws IOException {
		albumCoverBean.setCover(data);
	}
	
	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}
	
	@Required
	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}
}
