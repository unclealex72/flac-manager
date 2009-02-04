package uk.co.unclealex.music.core.model;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="albumCoverBean")
@Table(name="covers")
public class AlbumCoverBean extends KeyedBean<AlbumCoverBean> {

	private static final Comparator<AlbumCoverBean> COMPARATOR = new AlbumCoverComparator();

	private String i_url;
	private PictureBean i_coverPictureBean;
	private PictureBean i_thumbnailPictureBean;
	private AlbumCoverSize i_albumCoverSize;
	private String i_flacAlbumPath;
	private Date i_dateDownloaded;
	private Date i_dateSelected;
	private int i_coverLength;
	private int i_thumbnailLength;
	
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
	
	@Transient
	public byte[] getCover() {
		PictureBean coverPictureBean = getCoverPictureBean();
		return coverPictureBean==null?null:coverPictureBean.getPicture();
	}
	
	public void setCover(byte[] cover) {
		setCoverLength(cover.length);
		PictureBean coverPictureBean = getCoverPictureBean();
		if (coverPictureBean == null) {
			setCoverPictureBean(new PictureBean(cover));
		}
	}

	@Transient
	public byte[] getThumbnail() {
		PictureBean thumbnailPictureBean = getThumbnailPictureBean();
		return thumbnailPictureBean==null?null:thumbnailPictureBean.getPicture();
	}

	public void setThumbnail(byte[] thumbnail) {
		setThumbnailLength(thumbnail.length);
		PictureBean thumbnailPictureBean = getThumbnailPictureBean();
		if (thumbnailPictureBean == null) {
			setThumbnailPictureBean(new PictureBean(thumbnail));
		}
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
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	protected PictureBean getCoverPictureBean() {
		return i_coverPictureBean;
	}

	protected void setCoverPictureBean(PictureBean coverPictureBean) {
		i_coverPictureBean = coverPictureBean;
	}

	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	protected PictureBean getThumbnailPictureBean() {
		return i_thumbnailPictureBean;
	}

	protected void setThumbnailPictureBean(PictureBean thumbnailPictureBean) {
		i_thumbnailPictureBean = thumbnailPictureBean;
	}

	public int getCoverLength() {
		return i_coverLength;
	}

	public void setCoverLength(int coverLength) {
		i_coverLength = coverLength;
	}

	public int getThumbnailLength() {
		return i_thumbnailLength;
	}

	public void setThumbnailLength(int thumbnailLength) {
		i_thumbnailLength = thumbnailLength;
	}


}
