package uk.co.unclealex.flacconverter.encoded.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Table(name="track_data")
@Entity
public class TrackDataBean extends KeyedBean<TrackDataBean> {

	private byte[] i_track;
	private Integer i_length;
	
	public TrackDataBean() {
		super();
	}

	public TrackDataBean(byte[] track) {
		i_track = track;
		i_length = track.length;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Lob()
	@Column()
	public byte[] getTrack() {
		return i_track;
	}

	public void setTrack(byte[] track) {
		i_track = track;
	}

	@NotNull(message="You must explicitly set the length of track data.")
	public Integer getLength() {
		return i_length;
	}

	public void setLength(Integer length) {
		i_length = length;
	}
}
