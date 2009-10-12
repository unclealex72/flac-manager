package uk.co.unclealex.music.base.model;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.hibernate.model.KeyedBean;

@Entity(name="albumCoverBean")
@Table(name="covers")
public class AlbumCoverBean extends KeyedBean<AlbumCoverBean> {

	private static final Comparator<AlbumCoverBean> COMPARATOR = new AlbumCoverComparator();

	private String i_url;
	private DataBean i_coverDataBean;
	private String i_extension;
	private Long i_albumCoverSize;
	private String i_albumCode;
	private String i_artistCode;
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
		return getUrl();
	}

	@Override
	public int compareTo(AlbumCoverBean o) {
		return COMPARATOR.compare(this, o);
	}

	@Column(nullable=false, unique=true)
	public String getUrl() {
		return i_url;
	}

	public void setUrl(String url) {
		i_url = url;
	}

	@Column(nullable=false)
	public Long getAlbumCoverSize() {
		return i_albumCoverSize;
	}

	public void setAlbumCoverSize(Long albumCoverSize) {
		i_albumCoverSize = albumCoverSize;
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
	
	@NotNull
	@OneToOne(cascade=CascadeType.ALL)
	public DataBean getCoverDataBean() {
		return i_coverDataBean;
	}

	public void setCoverDataBean(DataBean coverDataBean) {
		i_coverDataBean = coverDataBean;
	}

	@Column(nullable=false)
	public String getArtistCode() {
		return i_artistCode;
	}

	public void setArtistCode(String artistCode) {
		i_artistCode = artistCode;
	}

	@Column(nullable=false)
	public String getAlbumCode() {
		return i_albumCode;
	}

	public void setAlbumCode(String albumCode) {
		i_albumCode = albumCode;
	}

	@Column(nullable=false)
	public String getExtension() {
		return i_extension;
	}

	public void setExtension(String extension) {
		i_extension = extension;
	}


}
