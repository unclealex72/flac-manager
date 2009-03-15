package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;

import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

public interface AlbumCoverAction {

	public AlbumCoverBean getAlbumCoverBean();

	public void setAlbumCoverBean(AlbumCoverBean albumCoverBean);

	public FlacAlbumBean getFlacAlbumBean();

	public void setFlacAlbumBean(FlacAlbumBean flacAlbumBean);
	
	public SortedSet<AlbumCoverBean> getAlbumCoverBeans();

}