package uk.co.unclealex.music.base.dao;

import java.util.Set;
import java.util.SortedSet;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

public interface AlbumCoverDao extends KeyedDao<AlbumCoverBean> {

	public SortedSet<AlbumCoverBean> getCoversForAlbum(String artistCode, String albumCode);

	public AlbumCoverBean findSelectedCoverForAlbum(String artistCode, String albumCode);
	
	public SortedSet<AlbumCoverBean> getSelected();

	public int countSelectedAlbums();

	public Set<String> findSelectedAlbumCoverFilePaths();
}
