package uk.co.unclealex.music.web.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedBean;

public class RemoveArtistFromCartAction extends RemoveFromCartAction {

	private EncodedArtistBean i_encodedArtist;
	
	@Override
	public List<EncodedBean> listBeansToRemove() {
		List<EncodedBean> encodedBeans = new LinkedList<EncodedBean>();
		EncodedArtistBean encodedArtist = getEncodedArtist();
		encodedBeans.add(encodedArtist);
		for (EncodedAlbumBean encodedAlbumBean : encodedArtist.getEncodedAlbumBeans()) {
			encodedBeans.add(encodedAlbumBean);
			encodedBeans.addAll(encodedAlbumBean.getEncodedTrackBeans());
		}
		return encodedBeans;
	}

	public EncodedArtistBean getEncodedArtist() {
		return i_encodedArtist;
	}

	public void setEncodedArtist(EncodedArtistBean encodedArtist) {
		i_encodedArtist = encodedArtist;
	}
}
