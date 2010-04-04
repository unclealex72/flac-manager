package uk.co.unclealex.music.base.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.visitor.EncodedVisitor;
import uk.co.unclealex.music.base.visitor.VisitorException;

@Table(
		name="encoded_tracks",
		uniqueConstraints = {@UniqueConstraint(columnNames={"url", "encoderBean_id"})})
@Entity(name="encodedTrackBean")
public class EncodedTrackBean extends CodedBean<EncodedTrackBean> implements EncodedBean {

	private String i_flacUrl;
	private EncoderBean i_encoderBean;
	private Long i_timestamp;
	private String i_title;
	private Integer i_trackNumber;
	
	private DataBean i_trackDataBean;
	private EncodedAlbumBean i_encodedAlbumBean;
	private SortedSet<OwnerBean> i_ownerBeans = new TreeSet<OwnerBean>();
	
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
	@Index(name="code")
	public String getCode() {
		return super.getCode();
	}


	@Override
	@NotNull
	public String getFilename() {
		return super.getFilename();
	}
	
	@Override
	public <R, E extends Exception> R accept(EncodedVisitor<R, E> encodedVisitor) {
		try {
			return encodedVisitor.visit(this);
		}
		catch (Exception e) {
			throw new VisitorException(e);
		}
	}
	
	@ManyToOne
	@NotNull
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

	@NotNull
	@OneToOne(cascade=CascadeType.ALL)
	public DataBean getTrackDataBean() {
		return i_trackDataBean;
	}

	public void setTrackDataBean(DataBean trackDataBean) {
		i_trackDataBean = trackDataBean;
	}

	@ManyToMany(cascade={CascadeType.ALL})
	@Sort(type=SortType.NATURAL)
	public SortedSet<OwnerBean> getOwnerBeans() {
		return i_ownerBeans;
	}

	public void setOwnerBeans(SortedSet<OwnerBean> ownerBeans) {
		i_ownerBeans = ownerBeans;
	}

}