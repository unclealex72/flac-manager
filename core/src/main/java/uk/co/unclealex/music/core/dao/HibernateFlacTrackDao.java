package uk.co.unclealex.music.core.dao;

import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;

@Repository
public class HibernateFlacTrackDao extends HibernateKeyedReadOnlyDao<FlacTrackBean> implements FlacTrackDao {

	@Autowired
	public HibernateFlacTrackDao(@Qualifier("flacSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public FlacTrackBean findByUrl(String url) {
		FlacTrackBean example = createExampleBean();
		example.setUrl(url);
		return (FlacTrackBean) createCriteria(example).uniqueResult();
	}
	
	@Override
	public int countTracks() {
		Criteria criteria = createCriteria(createExampleBean()).setProjection(Projections.count("url"));
		return (Integer) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlacTrackBean findTrackStartingWith(String url) {
		Query query = getSession().createQuery("from FlacTrackBean where url like :url and type = 'flc'");
		query.setString("url", url + "%");
		Iterator<FlacTrackBean> iter = query.iterate();
		return iter.hasNext()?iter.next():null;
	}
	
	@Override
	public FlacTrackBean createExampleBean() {
		FlacTrackBean example = new FlacTrackBean();
		example.setType("flc");
		return example;
	}	
}
