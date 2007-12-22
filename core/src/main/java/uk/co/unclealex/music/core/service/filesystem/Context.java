package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;

public class Context {

	private EncodedTrackBean i_encodedTrackBean;
	private EncodedAlbumBean i_encodedAlbumBean;
	private EncodedArtistBean i_encodedArtistBean;
	private EncoderBean i_encoderBean;
	private SortedSet<String> i_children;
	
	public EncodedTrackBean getEncodedTrackBean() {
		return i_encodedTrackBean;
	}
	public void setEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		i_encodedTrackBean = encodedTrackBean;
	}
	public EncodedAlbumBean getEncodedAlbumBean() {
		return i_encodedAlbumBean;
	}
	public void setEncodedAlbumBean(EncodedAlbumBean encodedAlbumBean) {
		i_encodedAlbumBean = encodedAlbumBean;
	}
	public EncodedArtistBean getEncodedArtistBean() {
		return i_encodedArtistBean;
	}
	public void setEncodedArtistBean(EncodedArtistBean encodedArtistBean) {
		i_encodedArtistBean = encodedArtistBean;
	}
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}
	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}
	public SortedSet<String> getChildren() {
		return i_children;
	}
	public void setChildren(SortedSet<String> children) {
		i_children = children;
	}
}
