package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;

public abstract class DaoFileVisitor<R, E extends Exception> extends Visitor<E> {

	public final R visit(FileBean fileBean) {
		throw new IllegalArgumentException("Unknown file type: " + fileBean.getClass());
	}

	public abstract R visit(EncodedTrackFileBean encodedTrackFileBean);
	
	public abstract R visit(DirectoryFileBean directoryFileBean);	
}
