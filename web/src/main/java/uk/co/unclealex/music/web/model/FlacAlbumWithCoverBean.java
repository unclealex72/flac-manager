package uk.co.unclealex.music.web.model;

import uk.co.unclealex.music.core.model.AlbumCoverBean;
import uk.co.unclealex.music.core.model.FlacAlbumBean;

public class FlacAlbumWithCoverBean implements Comparable<FlacAlbumWithCoverBean> {

	private FlacAlbumBean i_flacAlbumBean;
	private AlbumCoverBean i_albumCoverBean;
	
	public FlacAlbumWithCoverBean(FlacAlbumBean flacAlbumBean,
			AlbumCoverBean albumCoverBean) {
		super();
		i_flacAlbumBean = flacAlbumBean;
		i_albumCoverBean = albumCoverBean;
	}

	@Override
	public int compareTo(FlacAlbumWithCoverBean o) {
		return getFlacAlbumBean().compareTo(o.getFlacAlbumBean());
	}
	
	public FlacAlbumBean getFlacAlbumBean() {
		return i_flacAlbumBean;
	}
	public AlbumCoverBean getAlbumCoverBean() {
		return i_albumCoverBean;
	}
}
