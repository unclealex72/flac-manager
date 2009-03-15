package uk.co.unclealex.music.web.actions;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;

@Transactional
@Service
public class AlbumsForArtistActionImpl extends AbstractAlbumsAction implements AlbumsForArtistAction {

	private FlacArtistBean i_flacArtistBean;
	
	@Override
	public Collection<FlacAlbumBean> getAlbumsToDisplay() {
		return getFlacArtistBean().getFlacAlbumBeans();
	}

	@Override
	public boolean displaySelectedCovers() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.AlbumsForArtistAction#getFlacArtistBean()
	 */
	public FlacArtistBean getFlacArtistBean() {
		return i_flacArtistBean;
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.AlbumsForArtistAction#setFlacArtistBean(uk.co.unclealex.music.base.model.FlacArtistBean)
	 */
	public void setFlacArtistBean(FlacArtistBean flacArtistBean) {
		i_flacArtistBean = flacArtistBean;
	}
}
