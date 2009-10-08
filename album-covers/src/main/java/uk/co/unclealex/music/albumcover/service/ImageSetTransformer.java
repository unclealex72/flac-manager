package uk.co.unclealex.music.albumcover.service;

import java.awt.Image;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.AlbumCoverSize;

public class ImageSetTransformer implements Transformer<ImageSet, AlbumCoverBean> {

	@Override
	public AlbumCoverBean transform(ImageSet imageSet) {
		Image[] images = new Image[] { 
			imageSet.getLargeImage(), imageSet.getMediumImage(), imageSet.getSmallImage(), imageSet.getTinyImage() };
		AlbumCoverBean albumCoverBean = null;
		for (int idx = 0; albumCoverBean == null && idx < images.length; idx++) {
			Image image = images[idx];
			if (image != null) {
				String url = image.getURL();
				albumCoverBean = new AlbumCoverBean();
				albumCoverBean.setUrl(url);
				albumCoverBean.setAlbumCoverSize(AlbumCoverSize.values()[idx]);
			}
		}
		return albumCoverBean;
	}
}
