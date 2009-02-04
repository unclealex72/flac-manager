package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.AlbumCoverBean;

public interface AlbumCoverDao extends KeyedDao<AlbumCoverBean> {

	public SortedSet<AlbumCoverBean> getCoversForAlbumPath(String albumPath);

	public boolean albumPathHasCovers(String albumPath);
	
	public AlbumCoverBean findSelectedCoverForAlbumPath(String albumPath);
	
	public SortedSet<AlbumCoverBean> getSelected();
}
