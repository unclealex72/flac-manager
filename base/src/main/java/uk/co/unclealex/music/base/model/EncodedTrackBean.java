package uk.co.unclealex.music.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
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
	
	private KnownLengthInputStream i_trackData;
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

	@Lob
	@NotNull
	@Type(type="uk.co.unclealex.music.base.io.BlobUserType")
	@Columns(columns = {
	    @Column(name="trackData", length=Integer.MAX_VALUE),
	    @Column(name="trackDataLength")
	})
	public KnownLengthInputStream getTrackData() {
		return i_trackData;
	}

	public void setTrackData(KnownLengthInputStream trackData) {
		i_trackData = trackData;
	}

}
