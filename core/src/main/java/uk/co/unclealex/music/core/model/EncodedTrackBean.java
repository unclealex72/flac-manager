package uk.co.unclealex.music.core.model;

import java.util.Comparator;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections15.ComparatorUtils;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.core.visitor.EncodedVisitor;

@Table(
		name="encoded_tracks",
		uniqueConstraints = {@UniqueConstraint(columnNames={"url", "encoderBean_id"})})
@Entity(name="encodedTrackBean")
public class EncodedTrackBean extends AbstractEncodedBean<EncodedTrackBean> implements EncodedBean {

	private static final Comparator<EncodedTrackBean> ENCODER_COMPARATOR = 
		new Comparator<EncodedTrackBean>() {
		@Override
		public int compare(EncodedTrackBean o1, EncodedTrackBean o2) {
			return o1.getEncoderBean().compareTo(o2.getEncoderBean());
		}
	};
	private static final Comparator<EncodedTrackBean> ALBUM_COMPARATOR =
		new Comparator<EncodedTrackBean>() {
		@Override
		public int compare(EncodedTrackBean o1, EncodedTrackBean o2) {
			return 
				ComparatorUtils.nullHighComparator(
					EncodedAlbumBean.ENCODED_ALBUM_COMPARATOR).compare(
							o1.getEncodedAlbumBean(), 
							o2.getEncodedAlbumBean());
		}
	};
	private static final Comparator<EncodedTrackBean> TRACK_NUMBER_COMPARATOR =
		new Comparator<EncodedTrackBean>() {
		@Override
		public int compare(EncodedTrackBean o1, EncodedTrackBean o2) {
			return o1.getTrackNumber().compareTo(o2.getTrackNumber());
		}
	};

	private static final Comparator<EncodedTrackBean> URL_COMPARATOR =
		new Comparator<EncodedTrackBean>() {
		@Override
		public int compare(EncodedTrackBean o1, EncodedTrackBean o2) {
			return o1.getFlacUrl().compareTo(o2.getFlacUrl());
		}
	};
	
	protected static final Comparator<EncodedTrackBean> ENCODED_TRACK_BEAN_COMPARATOR = 
		new Comparator<EncodedTrackBean>() {
		@Override
		public int compare(EncodedTrackBean o1, EncodedTrackBean o2) {
			int cmp = ENCODER_COMPARATOR.compare(o1, o2);
			if (cmp != 0) {
				return cmp;
			}
			cmp = ALBUM_COMPARATOR.compare(o1, o2);
			if (cmp != 0) {
				return cmp;
			}
			cmp = TRACK_NUMBER_COMPARATOR.compare(o1, o2);
			return cmp != 0?cmp:URL_COMPARATOR.compare(o1, o2);
		}
	};

	private String i_flacUrl;
	private EncoderBean i_encoderBean;
	private Integer i_length;
	private Long i_timestamp;
	private String i_title;
	private Integer i_trackNumber;
	
	private SortedSet<TrackDataBean> i_trackDataBeans;
	private EncodedAlbumBean i_encodedAlbumBean;
	
	public EncodedTrackBean() {
		super();
	}
	
	@Override
	public int compareTo(EncodedTrackBean o) {
		return ENCODED_TRACK_BEAN_COMPARATOR.compare(this, o);
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

	@NotNull(message="You must explicitly set the length of track data.")
	public Integer getLength() {
		return i_length;
	}

	public void setLength(Integer length) {
		i_length = length;
	}

	@ManyToOne
	public EncodedAlbumBean getEncodedAlbumBean() {
		return i_encodedAlbumBean;
	}

	public void setEncodedAlbumBean(EncodedAlbumBean encodedAlbumBean) {
		i_encodedAlbumBean = encodedAlbumBean;
	}

	@OneToMany(mappedBy="encodedTrackBean", targetEntity=TrackDataBean.class, cascade = CascadeType.ALL)
	@Sort(type=SortType.NATURAL)
	public SortedSet<TrackDataBean> getTrackDataBeans() {
		return i_trackDataBeans;
	}

	public void setTrackDataBeans(SortedSet<TrackDataBean> trackDataBeans) {
		i_trackDataBeans = trackDataBeans;
	}

	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}

	public Integer getTrackNumber() {
		return i_trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		i_trackNumber = trackNumber;
	}

}
