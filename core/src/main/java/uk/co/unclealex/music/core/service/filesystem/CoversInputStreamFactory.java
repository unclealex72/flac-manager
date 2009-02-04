package uk.co.unclealex.music.core.service.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.AlbumCoverDao;

@Service
@Transactional
public class CoversInputStreamFactory implements Transformer<Integer, InputStream> {

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	public InputStream transform(Integer id) {
		byte[] cover = getAlbumCoverDao().findById(id).getCover();
		return new ByteArrayInputStream(cover);
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	@Required
	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

}
