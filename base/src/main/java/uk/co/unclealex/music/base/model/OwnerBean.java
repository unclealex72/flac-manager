package uk.co.unclealex.music.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import uk.co.unclealex.hibernate.model.KeyedBean;

@Table(name="owners")
@Entity
public class OwnerBean extends KeyedBean<OwnerBean> {

	private String i_name;

	public OwnerBean() {
	}
	
	public OwnerBean(String name) {
		super();
		i_name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(OwnerBean o) {
		return getName().compareTo(o.getName());
	}
	
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof OwnerBean) && ObjectUtils.equals(getName(), ((OwnerBean) obj).getName());
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@NotEmpty(message="An owner must have a name.")
	@Length(max=50, message="Please use a shorter name.")
	@Column(unique=true)
	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}
}
