package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

public class HibernateTrackDataDao extends HibernateEncodingDao<TrackDataBean> implements TrackDataDao {

	@Override
	public TrackDataBean createExampleBean() {
		return new TrackDataBean();
	}
}
