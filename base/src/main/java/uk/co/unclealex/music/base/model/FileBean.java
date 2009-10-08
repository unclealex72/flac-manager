package uk.co.unclealex.music.base.model;

import uk.co.unclealex.music.base.visitor.FileVisitor;

public interface FileBean {

	public Integer getId();

	public String getPath();

	public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor);
}