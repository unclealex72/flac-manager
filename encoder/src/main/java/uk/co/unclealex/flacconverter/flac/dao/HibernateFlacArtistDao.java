package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;

public class HibernateFlacArtistDao extends HibernateCodeDao<FlacArtistBean> implements
		FlacArtistDao {

	@Override
	public int countArtistsBeginningWith(char c) {
		return (Integer) createStartsWithCriteria(c).setProjection(Projections.count("code")).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<FlacArtistBean> getArtistsBeginningWith(char c) {
		return new TreeSet<FlacArtistBean>(createStartsWithCriteria(c).list());
	}
	
	protected Criteria createStartsWithCriteria(char c) {
		return createCriteria(createExampleBean()).add(Restrictions.like("code", Character.toUpperCase(c) + "%"));
	}
	
	@Override
	public FlacArtistBean createExampleBean() {
		return new FlacArtistBean();
	}

}
