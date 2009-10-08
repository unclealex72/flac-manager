package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.AbstractFileBean;
import uk.co.unclealex.music.base.model.DbDirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;

public abstract class DaoAwareFileVisitor<R, E extends Exception> extends Visitor<E> {

	public final R visit(AbstractFileBean fileBean) {
		throw new IllegalArgumentException("Unknown file type: " + fileBean.getClass());
	}

	public abstract R visit(EncodedTrackFileBean encodedTrackFileBean);
	
	public abstract R visit(DbDirectoryFileBean dbDirectoryFileBean);	
}
