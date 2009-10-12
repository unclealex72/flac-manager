package uk.co.unclealex.music.base.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Entity(name="directoryFileBean")
@DiscriminatorValue("directory")
public class DirectoryFileBean extends FileBean {

	private Set<FileBean> i_children = new HashSet<FileBean>();

	public <R, E extends Exception> R accept(DaoFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@OneToMany(targetEntity=FileBean.class, mappedBy="parent")
	public Set<FileBean> getChildren() {
		return i_children;
	}

	public void setChildren(Set<FileBean> children) {
		i_children = children;
	}
}
