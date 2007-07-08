package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import uk.co.unclealex.flacconverter.flac.model.CodedBean;

public interface CodeDao<T extends CodedBean<T>> {

	public T findByCode(String code);
	
	public SortedSet<T> getAll();
}
