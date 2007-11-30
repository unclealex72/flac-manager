package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Example;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

public class HibernateTrackDataDao extends HibernateEncodingDao<TrackDataBean> implements TrackDataDao {

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

	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<Integer> getIdsForEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		Query query =
			getSession().createQuery(
					"select t.id from encodedTrackBean e join e.trackDataBeans t where e = :encodedTrackBean").
			setParameter("encodedTrackBean", encodedTrackBean);
		return new TreeSet<Integer>(query.list());
	}
	
	public void removeById(int id) {
		getSession().createQuery("delete TrackDataBean where id = :id")
			.setInteger( "id", id )
		  .executeUpdate();
	}
}
