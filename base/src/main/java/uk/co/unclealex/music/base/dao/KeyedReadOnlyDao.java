package uk.co.unclealex.music.base.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.KeyedBean;

@Transactional
public interface KeyedReadOnlyDao<T extends KeyedBean<T>> {

	public SortedSet<T> getAll();
	public T findById(int id);
	public void dismiss(T keyedBean);
	public void flush();
	public void clear();

}
