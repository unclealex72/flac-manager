package uk.co.unclealex.music.core.visitor;

import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacArtistBean;
import uk.co.unclealex.music.core.model.FlacBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;

public abstract class FlacVisitor {

	public final void visit(FlacBean flacBean) {
		throw new IllegalStateException("An unknown flac bean was passed to a visitor: " + flacBean.getClass().getName());
	}
	
	public abstract void visit(FlacTrackBean flacTrackBean);
	
	public abstract void visit(FlacAlbumBean flacAlbumBean);
	
	public abstract void visit(FlacArtistBean flacArtistBean);
	

}
