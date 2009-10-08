package uk.co.unclealex.music.base.model;

import java.util.Set;

public interface DirectoryFileBean extends FileBean {

	public Set<FileBean> getChildren();

}