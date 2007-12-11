package uk.co.unclealex.music.encoder.flac.visitor;

import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.flac.model.FlacBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public abstract class FlacVisitor {

	public final void visit(FlacBean flacBean) {
		throw new IllegalStateException("An unknown flac bean was passed to a visitor: " + flacBean.getClass().getName());
	}
	
	public abstract void visit(FlacTrackBean flacTrackBean);
	
	public abstract void visit(FlacAlbumBean flacAlbumBean);
	
	public abstract void visit(FlacArtistBean flacArtistBean);
	

}