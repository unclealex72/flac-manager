package uk.co.unclealex.music.core.visitor;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

public abstract class EncodedTrackVisitor extends EncodedVisitor {

	@Override
	public void visit(EncodedAlbumBean encodedAlbumBean) {
		for (EncodedTrackBean encodedTrackBean : refresh(encodedAlbumBean).getEncodedTrackBeans()) {
			encodedTrackBean.accept(this);
		}
	}

	@Override
	public void visit(EncodedArtistBean encodedArtistBean) {
		for (EncodedAlbumBean encodedAlbumBean : refresh(encodedArtistBean).getEncodedAlbumBeans()) {
			encodedAlbumBean.accept(this);
		}
	}

	public EncodedTrackBean refresh(EncodedTrackBean encodedTrackBean) {
		return encodedTrackBean;
	}
	
	public EncodedAlbumBean refresh(EncodedAlbumBean encodedAlbumBean) {
		return encodedAlbumBean;
	}
	
	public EncodedArtistBean refresh(EncodedArtistBean encodedArtistBean) {
		return encodedArtistBean;
	}
}
