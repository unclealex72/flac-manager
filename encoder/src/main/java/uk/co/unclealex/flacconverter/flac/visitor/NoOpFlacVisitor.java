package uk.co.unclealex.flacconverter.flac.visitor;

import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

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
