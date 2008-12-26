package uk.co.unclealex.music.albumcover;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.albumcover.model.AlbumCoverBean;
import uk.co.unclealex.music.albumcover.model.AlbumCoverSize;

import com.amazon.webservices.awsecommerceservice._2008_10_06.Image;
import com.amazon.webservices.awsecommerceservice._2008_10_06.ImageSet;

@Service
public class ImageSetTransformer implements Transformer<ImageSet, AlbumCoverBean> {

	private static final Logger log = Logger.getLogger(ImageSetTransformer.class);
	
	@Override
	public AlbumCoverBean transform(ImageSet imageSet) {
		Image[] images = new Image[] { 
			imageSet.getLargeImage(), imageSet.getMediumImage(), imageSet.getSmallImage(), imageSet.getTinyImage() };
		AlbumCoverBean albumCoverBean = null;
		for (int idx = 0; albumCoverBean == null && idx < images.length; idx++) {
			Image image = images[idx];
			if (image != null) {
				String url = image.getURL();
				try {
					albumCoverBean = new AlbumCoverBean(new URL(url), AlbumCoverSize.values()[idx]);
				} catch (MalformedURLException e) {
					log.warn("Album cover " + url + " is not a valid url.");
				}
			}
		}
		return albumCoverBean;
	}
}
