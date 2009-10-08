package uk.co.unclealex.music.base.visitor;

import uk.co.unclealex.music.base.model.DataFileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.FileBean;

public abstract class FileVisitor<R, E extends Exception> extends Visitor<E> {

	public final R visit(FileBean fileBean) {
		throw new IllegalArgumentException("Unknown file type: " + fileBean.getClass());
	}
	
	public abstract R visit(DirectoryFileBean directoryFileBean);
	
	public abstract R visit(DataFileBean dataFileBean);
}
