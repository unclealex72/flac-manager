package uk.co.unclealex.music.base.model;

import java.util.Set;

public class SimpleDirectoryFileBean extends AbstractDirectoryFileBean {

	private Set<FileBean> i_children;
	
	public SimpleDirectoryFileBean(String path, Set<FileBean> children) {
		super(path);
		i_children = children;
	}

	@Override
	public Set<FileBean> getChildren() {
		return i_children;
	}
}
