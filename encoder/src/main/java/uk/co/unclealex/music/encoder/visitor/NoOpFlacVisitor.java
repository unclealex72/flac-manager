package uk.co.unclealex.music.encoder.visitor;

import uk.co.unclealex.music.encoder.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.model.FlacTrackBean;

public class NoOpFlacVisitor extends FlacVisitor {

	@Override
	public void visit(FlacTrackBean flacTrackBean) {
	}

	@Override
	public void visit(FlacAlbumBean flacAlbumBean) {
	}

	@Override
	public void visit(FlacArtistBean flacArtistBean) {
	}

}
