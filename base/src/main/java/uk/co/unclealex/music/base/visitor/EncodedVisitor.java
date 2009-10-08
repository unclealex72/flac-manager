package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

public abstract class EncodedVisitor<R, E extends Exception> extends Visitor<E> {

	public final void visit(EncodedBean encodedBean) {
		throw new IllegalStateException("An unknown encoded bean was passed to a visitor: " + encodedBean.getClass().getName());
	}
	
	public abstract R visit(EncodedTrackBean encodedTrackBean);
	
	public abstract R visit(EncodedAlbumBean encodedAlbumBean);
	
	public abstract R visit(EncodedArtistBean encodedArtistBean);
	

}
