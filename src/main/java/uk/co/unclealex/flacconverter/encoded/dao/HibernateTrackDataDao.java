package uk.co.unclealex.flacconverter.encoded.dao;

import java.io.IOException;
import java.sql.Blob;

import org.hibernate.Hibernate;

import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

public class HibernateTrackDataDao extends HibernateEncodedDao<TrackDataBean> implements TrackDataDao {

	public Blob createBlob(byte[] bytes) throws IOException {
		return Hibernate.createBlob(bytes);
	}
	
	@Override
	public TrackDataBean createExampleBean() {
		return new TrackDataBean();
	}
}
