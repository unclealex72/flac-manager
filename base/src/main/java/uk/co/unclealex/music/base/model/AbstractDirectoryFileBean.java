package uk.co.unclealex.music.base.model;

import uk.co.unclealex.music.base.visitor.FileVisitor;

public abstract class AbstractDirectoryFileBean implements DirectoryFileBean {

	private String i_path;
	
	public AbstractDirectoryFileBean(String path) {
		super();
		i_path = path;
	}

	@Override
	public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
		return fileVisitor.visit(this);
	}
	
	@Override
	public Integer getId() {
		return getPath().hashCode();
	}
	
	public String getPath() {
		return i_path;
	}	
}
