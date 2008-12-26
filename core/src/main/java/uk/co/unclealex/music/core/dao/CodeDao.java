package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.AbstractFlacBean;

public interface CodeDao<T extends AbstractFlacBean<T>> extends KeyedReadOnlyDao<T> {

	public T findByCode(String code);
	
	public SortedSet<T> getAll();
}
