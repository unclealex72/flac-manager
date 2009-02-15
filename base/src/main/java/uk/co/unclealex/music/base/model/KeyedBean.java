package uk.co.unclealex.music.base.model;

import java.io.Serializable;

public abstract class KeyedBean<T extends KeyedBean<T>> implements Comparable<T>, Serializable {

	private Integer i_id;

	@Override
	public abstract String toString();
	
	public int compareTo(T o) {
		Integer id = getId();
		if (id == null) {
			return o.getId()==null?0:1;
		}
		else {
			Integer otherId = o.getId();
			return otherId==null?-1:id - otherId;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().isAssignableFrom(getClass()) && this.compareTo((T) obj) == 0; 
	}
	
	@Override
	public int hashCode() {
		Integer id = getId();
		return id==null?super.hashCode():id.hashCode();
	}
	
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}	
}
