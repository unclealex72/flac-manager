package uk.co.unclealex.music.base.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.visitor.EncodedVisitor;

@Table(
		name="encoded_tracks",
		uniqueConstraints = {@UniqueConstraint(columnNames={"url", "encoderBean_id"})})
@Entity(name="encodedTrackBean")
public class EncodedTrackBean extends AbstractEncodedBean<EncodedTrackBean> implements EncodedBean {

	private String i_flacUrl;
	private EncoderBean i_encoderBean;
	private Long i_timestamp;
	private String i_title;
	private Integer i_trackNumber;
	
	private KnownLengthInputStreamBean i_trackDataBean;
	private EncodedAlbumBean i_encodedAlbumBean;
	
	public EncodedTrackBean() {
		super();
	}
	
	@Override
	public int compareTo(EncodedTrackBean o) {
		int cmp = getFlacUrl().compareTo(o.getFlacUrl());
		return cmp==0?getEncoderBean().getExtension().compareTo(o.getEncoderBean().getExtension()):cmp;
	}
	
	@Override
	public String toString() {
		return getEncoderBean().getExtension() + "->" + getFlacUrl();
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	@NotNull
	public String getFilename() {
		return super.getFilename();
	}
	
	@Override
	public void accept(EncodedVisitor encodedVisitor) {
		encodedVisitor.visit(this);
	}
	
	@Transient
	public KnownLengthInputStream getTrackData() {
		return getTrackDataBean().getData();
	}

	public void setTrackData(KnownLengthInputStream trackData) {
		KnownLengthInputStreamBean trackDataBean = getTrackDataBean();
		if (trackDataBean == null) {
			setTrackDataBean(new KnownLengthInputStreamBean(trackData));
		}
		else {
			trackDataBean.setData(trackData);
		}
	}


	@ManyToOne
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}

	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}

	@Column(name="url")
	@NotNull
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

	@ManyToOne
	public EncodedAlbumBean getEncodedAlbumBean() {
		return i_encodedAlbumBean;
	}

	public void setEncodedAlbumBean(EncodedAlbumBean encodedAlbumBean) {
		i_encodedAlbumBean = encodedAlbumBean;
	}

	@NotNull
	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}

	@NotNull
	public Integer getTrackNumber() {
		return i_trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		i_trackNumber = trackNumber;
	}

	@OneToOne(cascade=CascadeType.ALL)
	@NotNull
	protected KnownLengthInputStreamBean getTrackDataBean() {
		return i_trackDataBean;
	}

	protected void setTrackDataBean(KnownLengthInputStreamBean trackDataBean) {
		i_trackDataBean = trackDataBean;
	}

}