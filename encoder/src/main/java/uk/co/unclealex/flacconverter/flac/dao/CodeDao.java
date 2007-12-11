package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.dao.KeyedDao;
import uk.co.unclealex.music.encoder.flac.model.AbstractFlacBean;

public interface CodeDao<T extends AbstractFlacBean<T>> extends KeyedDao<T> {

	public T findByCode(String code);
	
	public SortedSet<T> getAll();
}
