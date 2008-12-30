package uk.co.unclealex.music.core.model;

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

@Entity(name="albumCoverBean")
@Table(name="covers")
public class AlbumCoverBean extends KeyedBean<AlbumCoverBean> {

	private static final Comparator<AlbumCoverBean> COMPARATOR = new AlbumCoverComparator();

	private String i_url;
	private byte[] i_cover;
	private AlbumCoverSize i_albumCoverSize;
	private String i_flacAlbumPath;
	private String i_extension;
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
	
	@Column(nullable=false)
	public String getUrl() {
		return i_url;
	}

	public void setUrl(String url) {
		i_url = url;
	}

	@Lob
	public byte[] getCover() {
		return i_cover;
	}

	public void setCover(byte[] cover) {
		i_cover = cover;
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
	public String getExtension() {
		return i_extension;
	}

	public void setExtension(String extension) {
		i_extension = extension;
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
}
