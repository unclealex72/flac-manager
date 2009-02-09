package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

public abstract class EncodedVisitor {

	public final void visit(EncodedBean encodedBean) {
		throw new IllegalStateException("An unknown encoded bean was passed to a visitor: " + encodedBean.getClass().getName());
	}
	
	public abstract void visit(EncodedTrackBean encodedTrackBean);
	
	public abstract void visit(EncodedAlbumBean encodedAlbumBean);
	
	public abstract void visit(EncodedArtistBean encodedArtistBean);
	

}
