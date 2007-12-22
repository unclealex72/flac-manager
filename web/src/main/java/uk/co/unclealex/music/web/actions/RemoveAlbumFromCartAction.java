package uk.co.unclealex.music.web.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedBean;

public class RemoveAlbumFromCartAction extends RemoveFromCartAction {

	private EncodedAlbumBean i_encodedAlbum;
	
	@Override
	public List<EncodedBean> listBeansToRemove() {
		List<EncodedBean> encodedBeans = new LinkedList<EncodedBean>();
		EncodedAlbumBean encodedAlbumBean = getEncodedAlbum();
		encodedBeans.add(encodedAlbumBean);
		encodedBeans.addAll(encodedAlbumBean.getEncodedTrackBeans());
		return encodedBeans;
	}
	
	public EncodedAlbumBean getEncodedAlbum() {
		return i_encodedAlbum;
	}

	public void setEncodedAlbum(EncodedAlbumBean encodedAlbum) {
		i_encodedAlbum = encodedAlbum;
	}
}
