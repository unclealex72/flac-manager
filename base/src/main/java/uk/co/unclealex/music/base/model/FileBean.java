package uk.co.unclealex.music.base.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="filetype", discriminatorType=DiscriminatorType.STRING)
@Table(name="files")
@Entity(name="fileBean")
public abstract class FileBean extends KeyedBean<FileBean> {

	private String i_path;
	private DirectoryFileBean i_parent;
	private Date i_modificationTimestamp;
	private Date i_creationTimestamp;
	
	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	public <R, E extends Exception> R accept(DaoFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.model.FileBean#getPath()
	 */
	@Index(name="path")
	public String getPath() {
		return i_path;
	}

	public void setPath(String path) {
		i_path = path;
	}

	@ManyToOne
	public DirectoryFileBean getParent() {
		return i_parent;
	}

	public void setParent(DirectoryFileBean parent) {
		i_parent = parent;
	}

	@Column(nullable=false)
	public Date getModificationTimestamp() {
		return i_modificationTimestamp;
	}

	public void setModificationTimestamp(Date modificationTimestamp) {
		i_modificationTimestamp = modificationTimestamp;
	}

	@Column(nullable=false)
	public Date getCreationTimestamp() {
		return i_creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		i_creationTimestamp = creationTimestamp;
	}
}
