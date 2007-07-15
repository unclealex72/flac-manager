package uk.co.unclealex.flacconverter.encoded.dao;

import java.io.IOException;
import java.sql.Blob;

import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

public interface TrackDataDao extends EncodingDao<TrackDataBean> {

	public Blob createBlob(byte[] bytes) throws IOException;

}
