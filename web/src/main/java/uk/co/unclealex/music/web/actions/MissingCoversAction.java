package uk.co.unclealex.music.web.actions;

import java.util.Collection;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

public class MissingCoversAction extends AbstractAlbumsAction {

	private AlbumCoverService i_albumCoverService;
	
	@Override
	public Collection<FlacAlbumBean> getAlbumsToDisplay() {
		return getAlbumCoverService().findAlbumsWithoutCovers();
	}

	@Override
	public boolean displaySelectedCovers() {
		return true;
	}
	
	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}

}
