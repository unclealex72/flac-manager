package uk.co.unclealex.music.web.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.flacconverter.RemoveFromCartAction;
import uk.co.unclealex.music.web.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.web.flac.model.FlacArtistBean;
import uk.co.unclealex.music.web.flac.model.FlacBean;

public class RemoveArtistFromCartAction extends RemoveFromCartAction {

	private FlacArtistBean i_flacArtist;
	
	@Override
	public List<FlacBean> listBeansToRemove() {
		List<FlacBean> flacBeans = new LinkedList<FlacBean>();
		FlacArtistBean flacArtist = getFlacArtist();
		flacBeans.add(flacArtist);
		for (FlacAlbumBean flacAlbumBean : flacArtist.getFlacAlbumBeans()) {
			flacBeans.add(flacAlbumBean);
			flacBeans.addAll(flacAlbumBean.getFlacTrackBeans());
		}
		return flacBeans;
	}

	public FlacArtistBean getFlacArtist() {
		return i_flacArtist;
	}

	public void setFlacArtist(FlacArtistBean flacArtist) {
		i_flacArtist = flacArtist;
	}
}
