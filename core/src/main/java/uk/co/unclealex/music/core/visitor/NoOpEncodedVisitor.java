package uk.co.unclealex.music.core.visitor;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

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
