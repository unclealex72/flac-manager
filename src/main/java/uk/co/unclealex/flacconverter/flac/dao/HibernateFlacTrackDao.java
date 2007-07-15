package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.encoded.dao.HibernateKeyedDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class HibernateFlacTrackDao extends HibernateKeyedDao<FlacTrackBean> implements FlacTrackDao {

	public FlacTrackBean findByUrl(String url) {
		FlacTrackBean example = createExampleBean();
		example.setUrl(url);
		return (FlacTrackBean) createCriteria(example).uniqueResult();
	}
	
	@Override
	public FlacTrackBean createExampleBean() {
		FlacTrackBean example = new FlacTrackBean();
		example.setType("flc");
		return example;
	}	
}
