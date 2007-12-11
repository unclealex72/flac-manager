package uk.co.unclealex.music.encoder.flac.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import uk.co.unclealex.music.core.dao.HibernateKeyedDao;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public class HibernateFlacTrackDao extends HibernateKeyedDao<FlacTrackBean> implements FlacTrackDao {

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
	
	@Override
	public FlacTrackBean createExampleBean() {
		FlacTrackBean example = new FlacTrackBean();
		example.setType("flc");
		return example;
	}	
}
