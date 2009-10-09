package uk.co.unclealex.music.base.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import uk.co.unclealex.music.base.visitor.DaoAwareFileVisitor;
import uk.co.unclealex.music.base.visitor.FileVisitor;

@Entity(name="directoryFileBean")
@DiscriminatorValue("directory")
public class DbDirectoryFileBean extends AbstractFileBean implements DirectoryFileBean {

	private Set<FileBean> i_children = new HashSet<FileBean>();

	@Override
	public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
		return fileVisitor.visit(DbDirectoryFileBean.this);
	}
	
	public <R, E extends Exception> R accept(DaoAwareFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@OneToMany(targetEntity=AbstractFileBean.class, mappedBy="parent")
	public Set<FileBean> getChildren() {
		return i_children;
	}

	public void setChildren(Set<FileBean> children) {
		i_children = children;
	}
}
