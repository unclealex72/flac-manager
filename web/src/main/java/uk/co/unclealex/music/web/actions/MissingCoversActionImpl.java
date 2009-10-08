package uk.co.unclealex.music.web.actions;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.FlacAlbumBean;

@Transactional
public class MissingCoversActionImpl extends AbstractAlbumsAction {

	@Override
	public Collection<FlacAlbumBean> getAlbumsToDisplay() {
		return getAlbumCoverService().findAlbumsWithoutCovers();
	}

	@Override
	public boolean displaySelectedCovers() {
		return true;
	}	
}
