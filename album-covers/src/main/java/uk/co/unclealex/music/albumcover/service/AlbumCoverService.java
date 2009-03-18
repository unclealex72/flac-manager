package uk.co.unclealex.music.albumcover.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.SortedSet;

import javax.jcr.RepositoryException;

import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.AlbumCoverSize;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

public interface AlbumCoverService {

	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public void downloadMissing();

	public void saveSelectedAlbumCovers();

	public AlbumCoverBean findSelectedCoverForFlacAlbum(FlacAlbumBean flacAlbumBean);

	public void downloadAndSaveCoversForAlbums(Collection<FlacAlbumBean> flacAlbumBeans);
	
	public void removeUnselectedCovers(FlacAlbumBean flacAlbumBean);
	
	public AlbumCoverBean saveAndSelectCover(
			FlacAlbumBean flacAlbumBean, String imageUrl, InputStream urlInputStream, AlbumCoverSize albumCoverSize) throws RepositoryException, IOException;

	public void selectAlbumCover(AlbumCoverBean albumCoverBean) throws RepositoryException;

	public Predicate<FlacAlbumBean> createAlbumHasCoverPredicate();
	
	public SortedSet<FlacAlbumBean> findAlbumsWithoutCovers();
}
