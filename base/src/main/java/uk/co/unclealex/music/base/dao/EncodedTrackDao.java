package uk.co.unclealex.music.base.dao;

import java.util.Set;
import java.util.SortedSet;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface EncodedTrackDao extends KeyedDao<EncodedTrackBean> {

	public EncodedTrackBean findByArtistCodeAndAlbumCodeAndCode(String artistCode, String albumCode, String trackCode);

	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean);

	public SortedSet<EncodedTrackBean> findByUrl(String url);

	public SortedSet<EncodedTrackBean> findByOwnerBean(OwnerBean ownerBean);

	public SortedSet<EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean);

	public SortedSet<EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean);

	public SortedSet<EncodedTrackBean> findByAlbumAndEncoderBean(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean);
	
	public SortedSet<EncodedTrackBean> findTracksWithoutAnAlbum();

	public EncodedTrackBean findByAlbumAndEncoderBeanAndTrackNumber(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean,
			int trackNumber);

	public SortedSet<EncodedTrackBean> findByArtist(EncodedArtistBean encodedArtistBean);

	public Set<EncodedTrackBean> getAllOrphanedTracks(SortedSet<String> allFlacUrls);

	public SortedSet<EncodedTrackBean> getAllWithOwners();

	public SortedSet<EncodedTrackBean> findByAlbumAndEncoderCoverSupported(EncodedAlbumBean encodedAlbumBean, boolean b);

	public SortedSet<EncodedTrackBean> findTracksEncodedAfter(long lastSyncTimestamp, OwnerBean ownerBean, EncoderBean encoderBean);

	public EncodedTrackBean findByCodesAndEncoderAndOwner(String artistCode, String albumCode, int trackNumber,
			String trackCode, OwnerBean ownerBean, EncoderBean encoderBean);
}
