package uk.co.unclealex.music.albumcover;

import java.util.SortedSet;

import uk.co.unclealex.music.albumcover.model.AlbumCoverBean;
import uk.co.unclealex.music.core.model.FlacAlbumBean;

public interface AlbumCoverService {

	public SortedSet<AlbumCoverBean> getDownloadedCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean);
}
