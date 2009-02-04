package uk.co.unclealex.music.web.transformer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.AlbumCoverBean;

@Transactional
@Service("thumbnailTransformer")
public class ThumbnailTransformer extends AbstractPictureTransformer {

	@Override
	public byte[] getPicture(AlbumCoverBean albumCoverBean) {
		return albumCoverBean.getThumbnail();
	}

}
