package uk.co.unclealex.music.web.transformer;

import org.apache.commons.collections15.Transformer;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.AlbumCoverDao;
import uk.co.unclealex.music.core.model.AlbumCoverBean;

@Transactional
public abstract class AbstractPictureTransformer implements Transformer<Integer, byte[]>{

	private AlbumCoverDao i_albumCoverDao;
	
	@Override
	public byte[] transform(Integer id) {
		return getPicture(getAlbumCoverDao().findById(id));
	}
	
	public abstract byte[] getPicture(AlbumCoverBean albumCoverBean);

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}
}
