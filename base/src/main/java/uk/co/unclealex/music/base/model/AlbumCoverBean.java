package uk.co.unclealex.music.base.model;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.base.io.KnownLengthInputStream;

@Entity(name="albumCoverBean")
@Table(name="covers")
public class AlbumCoverBean extends KeyedBean<AlbumCoverBean> {

	private static final Comparator<AlbumCoverBean> COMPARATOR = new AlbumCoverComparator();

	private String i_url;
	private KnownLengthInputStreamBean i_coverBean;
	private KnownLengthInputStreamBean i_thumbnailBean;
	private AlbumCoverSize i_albumCoverSize;
	private String i_flacAlbumPath;
	private Date i_dateDownloaded;
	private Date i_dateSelected;
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}
	
	@Override
	public String toString() {
		return getUrl() + "(" + getAlbumCoverSize() + ")";
	}

	@Override
	public int compareTo(AlbumCoverBean o) {
		return COMPARATOR.compare(this, o);
	}

	@Transient
	public String getExtension() {
		return "png";
	}
	
	@Column(nullable=false)
	public String getUrl() {
		return i_url;
	}

	public void setUrl(String url) {
		i_url = url;
	}

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	public AlbumCoverSize getAlbumCoverSize() {
		return i_albumCoverSize;
	}

	public void setAlbumCoverSize(AlbumCoverSize albumCoverSize) {
		i_albumCoverSize = albumCoverSize;
	}

	@Column(nullable=false)
	public String getFlacAlbumPath() {
		return i_flacAlbumPath;
	}
	
	public void setFlacAlbumPath(String flacAlbumPath) {
		i_flacAlbumPath = flacAlbumPath;
	}
	
	@Column(nullable=false)
	public Date getDateDownloaded() {
		return i_dateDownloaded;
	}

	public void setDateDownloaded(Date dateDownloaded) {
		i_dateDownloaded = dateDownloaded;
	}

	public Date getDateSelected() {
		return i_dateSelected;
	}

	public void setDateSelected(Date dateSelected) {
		i_dateSelected = dateSelected;
	}
	
	@Transient
	public KnownLengthInputStream getCover() {
		return getCoverBean().getData();
	}

	public void setCover(KnownLengthInputStream cover) {
		KnownLengthInputStreamBean coverBean = getCoverBean();
		if (coverBean == null) {
			setCoverBean(new KnownLengthInputStreamBean(cover));
		}
		else {
			coverBean.setData(cover);
		}
	}

	@Transient
	public KnownLengthInputStream getThumbnail() {
		return getThumbnailBean().getData();
	}

	public void setThumbnail(KnownLengthInputStream thumbnail) {
		KnownLengthInputStreamBean thumbnailBean = getThumbnailBean();
		if (thumbnailBean == null) {
			setThumbnailBean(new KnownLengthInputStreamBean(thumbnail));
		}
		else {
			thumbnailBean.setData(thumbnail);
		}
	}

	@OneToOne(cascade=CascadeType.ALL)
	@NotNull
	public KnownLengthInputStreamBean getCoverBean() {
		return i_coverBean;
	}

	public void setCoverBean(KnownLengthInputStreamBean coverBean) {
		i_coverBean = coverBean;
	}

	@OneToOne(cascade=CascadeType.ALL)
	@NotNull
	public KnownLengthInputStreamBean getThumbnailBean() {
		return i_thumbnailBean;
	}

	public void setThumbnailBean(KnownLengthInputStreamBean thumbnailBean) {
		i_thumbnailBean = thumbnailBean;
	}


}
