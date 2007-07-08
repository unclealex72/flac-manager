package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class HibernateFlacTrackDao extends HibernateDaoSupport implements FlacTrackDao {

	@SuppressWarnings("unchecked")
	public SortedSet<FlacTrackBean> getAllTracks() {
		FlacTrackBean example = createExample();
		example.setType("flc");
		SortedSet<FlacTrackBean> all = new TreeSet<FlacTrackBean>();
		all.addAll(createExampleCriteria(example).list());
		return all;
	}

	public FlacTrackBean findByUrl(String url) {
		FlacTrackBean example = createExample();
		example.setUrl(url);
		return (FlacTrackBean) createExampleCriteria(example).uniqueResult();
	}
	
	protected FlacTrackBean createExample() {
		FlacTrackBean example = new FlacTrackBean();
		example.setType("flc");
		return example;
	}
	
	protected Criteria createExampleCriteria(FlacTrackBean example) {
		return getSession().createCriteria(example.getClass()).add(Example.create(example));
	}
}
