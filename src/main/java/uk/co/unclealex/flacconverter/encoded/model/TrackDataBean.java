package uk.co.unclealex.flacconverter.encoded.model;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name="track_data")
@Entity
public class TrackDataBean extends KeyedBean<TrackDataBean> {

	private Blob i_track;
	
	public TrackDataBean() {
		super();
	}

	public TrackDataBean(Blob track) {
		super();
		i_track = track;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Lob
	public Blob getTrack() {
		return i_track;
	}

	public void setTrack(Blob track) {
		i_track = track;
	}
}
