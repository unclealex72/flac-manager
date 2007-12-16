package uk.co.unclealex.music.encoder.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.encoder.model.AbstractFlacBean;

public interface CodeDao<T extends AbstractFlacBean<T>> extends KeyedReadOnlyDao<T> {

	public T findByCode(String code);
	
	public SortedSet<T> getAll();
}
