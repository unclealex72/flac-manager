package uk.co.unclealex.music.base.dao;

import org.hibernate.Criteria;

public interface CriteriaFactory {

	public Criteria createCriteria();
}
