package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.TrackDataBean;

@Repository
@Transactional
public class HibernateTrackDataDao extends HibernateKeyedDao<TrackDataBean> implements TrackDataDao {

	@Autowired
	public HibernateTrackDataDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public TrackDataBean createExampleBean() {
		return new TrackDataBean();
	}

	@Override
	public TrackDataBean findByEncodedTrackBeanAndSequence(
			EncodedTrackBean encodedTrackBean, int sequence) {
		TrackDataBean exampleBean = new TrackDataBean();
		exampleBean.setSequence(sequence);
		Criteria criteria = 
			createCriteria(exampleBean).createCriteria("encodedTrackBean").add(Example.create(encodedTrackBean));
		return (TrackDataBean) criteria.uniqueResult();
	}

	@Override
	public SortedSet<Integer> getIdsForEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		Query query =
			getSession().createQuery(
					"select t.id from encodedTrackBean e join e.trackDataBeans t where e = :encodedTrackBean").
			setParameter("encodedTrackBean", encodedTrackBean);
		return asSortedSet(query, Integer.class);
	}
	
	@Override
	public SortedSet<Integer> getAllIds() {
		Query query = getSession().createQuery("select id from trackDataBean");
		return asSortedSet(query, Integer.class);
	}
	
	public void removeById(int id) {
		getSession().createQuery("delete trackDataBean where id = :id")
			.setInteger( "id", id )
		  .executeUpdate();
	}
	
	
}
