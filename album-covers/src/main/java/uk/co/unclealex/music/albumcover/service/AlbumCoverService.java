package uk.co.unclealex.music.albumcover.service;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.SortedSet;

import uk.co.unclealex.music.core.model.AlbumCoverBean;
import uk.co.unclealex.music.core.model.AlbumCoverSize;
import uk.co.unclealex.music.core.model.FlacAlbumBean;

public interface AlbumCoverService {

	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public void downloadMissing();

	public void saveSelectedAlbumCovers();

	public AlbumCoverBean findSelectedCoverForFlacAlbum(FlacAlbumBean flacAlbumBean);

	public void downloadAndSaveCoversForAlbums(Collection<FlacAlbumBean> flacAlbumBeans);
	
	public void removeUnselectedCovers(FlacAlbumBean flacAlbumBean);
	
	public void purgeCovers();
	
	public AlbumCoverBean saveAndSelectCover(FlacAlbumBean flacAlbumBean, String imageUrl, AlbumCoverSize albumCoverSize) throws IOException;

	public void selectAlbumCover(AlbumCoverBean albumCoverBean);

	public void resizeCover(AlbumCoverBean albumCoverBean, Dimension maximumSize, String extension, OutputStream out) throws IOException;
}
