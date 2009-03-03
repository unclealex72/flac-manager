package uk.co.unclealex.music.base.dao;

import java.util.SortedSet;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

public interface AlbumCoverDao extends KeyedDao<AlbumCoverBean> {

	public SortedSet<AlbumCoverBean> getCoversForAlbumPath(String albumPath);

	public boolean albumPathHasCovers(String albumPath);
	
	public AlbumCoverBean findSelectedCoverForAlbumPath(String albumPath);
	
	public SortedSet<AlbumCoverBean> getSelected();
}
