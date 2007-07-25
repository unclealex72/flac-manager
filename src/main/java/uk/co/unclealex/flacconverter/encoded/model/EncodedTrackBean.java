package uk.co.unclealex.flacconverter.encoded.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

@Table(
		name="encoded_tracks",
		uniqueConstraints = {@UniqueConstraint(columnNames={"url", "encoderbean_id"})})
@Entity
public class EncodedTrackBean extends KeyedBean<EncodedTrackBean> {

	private String i_flacUrl;
	private EncoderBean i_encoderBean;
	private TrackDataBean i_trackDataBean;
	private Long i_timestamp;
	
	
	public EncodedTrackBean() {
		super();
	}

	public EncodedTrackBean(byte[] data) {
		this();
		setTrackDataBean(new TrackDataBean(data));
	}
	
	@Override
	public int compareTo(EncodedTrackBean o) {
		int cmp = getEncoderBean().compareTo(o.getEncoderBean());
		return cmp==0?getFlacUrl().compareTo(o.getFlacUrl()):cmp;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Transient
	public InputStream getTrack() {
		TrackDataBean trackDataBean = getTrackDataBean();
		return trackDataBean==null?null:new ByteArrayInputStream(trackDataBean.getTrack());
	}
	
	@ManyToOne
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}

	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}

	@Column(name="url")
	public String getFlacUrl() {
		return i_flacUrl;
	}

	public void setFlacUrl(String flacUrl) {
		i_flacUrl = flacUrl;
	}

	@NotNull(message="You must supply a timestamp.")
	public Long getTimestamp() {
		return i_timestamp;
	}

	public void setTimestamp(Long timestamp) {
		i_timestamp = timestamp;
	}

	@OneToOne(optional=false, cascade={ CascadeType.ALL }, fetch = FetchType.LAZY)
	public TrackDataBean getTrackDataBean() {
		return i_trackDataBean;
	}

	public void setTrackDataBean(TrackDataBean trackDataBean) {
		i_trackDataBean = trackDataBean;
	}	

}
