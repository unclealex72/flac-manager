package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

public class DirectoryInformationBean extends PathInformationBean {

	private SortedSet<String> i_children = new TreeSet<String>();
	
	public DirectoryInformationBean(String path) {
		super(path, 0);
	}

	@Override
	public void accept(PathInformationBeanVisitor visitor) {
		visitor.visit(this);
	}

	public SortedSet<String> getChildren() {
		return i_children;
	}

	public void setChildren(SortedSet<String> children) {
		i_children = children;
	}

}
