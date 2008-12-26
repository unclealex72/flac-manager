package uk.co.unclealex.music.core.visitor;

import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacArtistBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;

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
