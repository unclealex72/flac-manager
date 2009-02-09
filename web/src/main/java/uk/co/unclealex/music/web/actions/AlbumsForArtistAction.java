package uk.co.unclealex.music.web.actions;

import java.util.Collection;

import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;

public class AlbumsForArtistAction extends AbstractAlbumsAction {

	private FlacArtistBean i_flacArtistBean;
	
	@Override
	public Collection<FlacAlbumBean> getAlbumsToDisplay() {
		return getFlacArtistBean().getFlacAlbumBeans();
	}

	@Override
	public boolean displaySelectedCovers() {
		return true;
	}
	
	public FlacArtistBean getFlacArtistBean() {
		return i_flacArtistBean;
	}

	public void setFlacArtistBean(FlacArtistBean flacArtistBean) {
		i_flacArtistBean = flacArtistBean;
	}
}
