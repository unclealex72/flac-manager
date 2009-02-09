package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.KeyedBean;

public interface KeyedDao<T extends KeyedBean<T>> extends KeyedReadOnlyDao<T> {

	public void store(T keyedBean);
	public void remove(T keyedBean);

}
