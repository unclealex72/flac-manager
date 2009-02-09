package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;

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
