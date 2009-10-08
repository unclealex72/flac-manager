package uk.co.unclealex.music.base.service;

import java.util.Set;
import java.util.SortedSet;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public interface EncodedService {

	public SortedSet<Character> getAllFirstLettersOfArtists();

	public EncodedArtistBean createArtist(FlacArtistBean flacArtistBean);

	public EncodedAlbumBean createAlbum(EncodedArtistBean encodedArtistBean, FlacAlbumBean flacAlbumBean);

	public EncodedTrackBean createTrack(EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean, FlacTrackBean flacTrackBean);

	public Set<EncodedTrackBean> findOrphanedEncodedTrackBeans();
}
