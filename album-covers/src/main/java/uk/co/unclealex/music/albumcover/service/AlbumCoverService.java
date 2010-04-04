package uk.co.unclealex.music.albumcover.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;

import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.service.ArtworkTaggingException;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;

public interface AlbumCoverService {

	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean) throws ExternalCoverArtException, IOException, ArtworkTaggingException;
	
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean);
	
	public void downloadMissing() throws ArtworkTaggingException;

	public AlbumCoverBean findSelectedCoverForFlacAlbum(FlacAlbumBean flacAlbumBean);

	/**
	 * Delete covers for an album that no longer exists.
	 * @param artistCode The code of the artist.
	 * @param albumCode The code of the album.
	 * @return True if any covers are removed, false otherwise.
	 */
	public boolean removeCoversForMissingAlbum(String artistCode, String albumCode);

	public void removeUnselectedCovers(FlacAlbumBean flacAlbumBean);
	
	public AlbumCoverBean saveAndSelectCover(FlacAlbumBean flacAlbumBean, String imageUrl, InputStream urlStream) throws IOException, ArtworkTaggingException;

	public void selectAlbumCover(AlbumCoverBean albumCoverBean, boolean updateTimestamp) throws IOException, ArtworkTaggingException;

	public Predicate<FlacAlbumBean> createAlbumHasCoverPredicate();
	
	public SortedSet<FlacAlbumBean> findAlbumsWithoutCovers();

	public AlbumCoverBean findSelectedCoverForEncodedTrack(EncodedTrackBean encodedTrackBean);

	public boolean tagFile(EncodedTrackBean encodedTrackBean, boolean updateTimestamp) throws IOException, ArtworkTaggingException;

	public int tagAll(boolean updateTimestamps);
}
