package uk.co.unclealex.music.core.dao;

import org.hibernate.Criteria;

public interface CriteriaFactory {

	public Criteria createCriteria();
}
