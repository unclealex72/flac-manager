package uk.co.unclealex.music.core.service.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.AlbumCoverDao;
import uk.co.unclealex.music.core.model.AlbumCoverBean;

@Service
@Transactional
public class CoverInputStreamCreator implements Transformer<Integer, InputStream> {

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	public InputStream transform(Integer id) {
		AlbumCoverBean albumCoverBean = getAlbumCoverDao().findById(id);
		return new ByteArrayInputStream(albumCoverBean.getCover());
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	@Required
	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}
}
