package uk.co.unclealex.music.base.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.visitor.DaoAwareFileVisitor;

@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="filetype", discriminatorType=DiscriminatorType.STRING)
@Table(name="files")
@Entity(name="fileBean")
public abstract class AbstractFileBean extends KeyedBean<AbstractFileBean> implements FileBean {

	private String i_path;
	private DbDirectoryFileBean i_parent;
	
	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	public <R, E extends Exception> R accept(DaoAwareFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.model.FileBean#getPath()
	 */
	public String getPath() {
		return i_path;
	}

	public void setPath(String path) {
		i_path = path;
	}

	@ManyToOne
	public DbDirectoryFileBean getParent() {
		return i_parent;
	}

	public void setParent(DbDirectoryFileBean parent) {
		i_parent = parent;
	}
}
