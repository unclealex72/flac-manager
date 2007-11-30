package uk.co.unclealex.flacconverter.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacBean;

public class RemoveAlbumFromCartAction extends RemoveFromCartAction {

	private FlacAlbumBean i_flacAlbum;
	
	@Override
	public List<FlacBean> listBeansToRemove() {
		List<FlacBean> flacBeans = new LinkedList<FlacBean>();
		FlacAlbumBean flacAlbumBean = getFlacAlbum();
		flacBeans.add(flacAlbumBean);
		flacBeans.addAll(flacAlbumBean.getFlacTrackBeans());
		return flacBeans;
	}
	
	public FlacAlbumBean getFlacAlbum() {
		return i_flacAlbum;
	}

	public void setFlacAlbum(FlacAlbumBean flacAlbum) {
		i_flacAlbum = flacAlbum;
	}
}
