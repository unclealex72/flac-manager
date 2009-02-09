package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

public class NoOpEncodedVisitor extends EncodedVisitor {

	@Override
	public void visit(EncodedTrackBean encodedTrackBean) {
	}

	@Override
	public void visit(EncodedAlbumBean encodedAlbumBean) {
	}

	@Override
	public void visit(EncodedArtistBean encodedArtistBean) {
	}

}
