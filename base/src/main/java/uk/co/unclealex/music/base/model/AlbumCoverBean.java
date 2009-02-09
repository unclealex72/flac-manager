package uk.co.unclealex.music.base.model;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.base.io.KnownLengthInputStream;

@Entity(name="albumCoverBean")
@Table(name="covers")
public class AlbumCoverBean extends KeyedBean<AlbumCoverBean> {

	private static final Comparator<AlbumCoverBean> COMPARATOR = new AlbumCoverComparator();

	private String i_url;
	private KnownLengthInputStream i_cover;
	private KnownLengthInputStream i_thumbnail;
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
	
	@Lob
	@NotNull
	@Type(type="uk.co.unclealex.music.base.io.BlobUserType")
	@Columns(columns = {
	    @Column(name="cover"),
	    @Column(name="coverLength")
	})
	public KnownLengthInputStream getCover() {
		return i_cover;
	}

	public void setCover(KnownLengthInputStream cover) {
		i_cover = cover;
	}

	@Lob
	@NotNull
	@Type(type="uk.co.unclealex.music.base.io.BlobUserType")
	@Columns(columns = {
	    @Column(name="thumbnail"),
	    @Column(name="thumbnailLength")
	})
	public KnownLengthInputStream getThumbnail() {
		return i_thumbnail;
	}

	public void setThumbnail(KnownLengthInputStream thumbnail) {
		i_thumbnail = thumbnail;
	}


}
