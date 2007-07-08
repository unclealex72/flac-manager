package uk.co.unclealex.flacconverter.encoded.model;

import java.util.Comparator;

import org.apache.commons.collections15.comparators.NullComparator;

public class KeyedBean<T extends KeyedBean<T>> implements Comparable<T> {

	private Integer i_id;

	public int compareTo(T o) {
		return
			new NullComparator<Integer>(
					new Comparator<Integer>() {
						public int compare(Integer o1, Integer o2) {
							return o1.compareTo(o2);
						}
					}).compare(getId(), o.getId());
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
