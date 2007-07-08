package uk.co.unclealex.flacconverter.flac.model;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@org.hibernate.annotations.Entity(mutable=false)
@Table(name="tracks")
public class FlacTrackBean extends CodedBean<FlacTrackBean> {

	private static String s_urlPrefix = "file://";
	private static int s_urlPrefixLength = s_urlPrefix.length();
	
	private String i_url;
	private byte[] i_rawTitle;
	private Long i_timestamp;
	private FlacAlbumBean i_flacAlbumBean;
	private String i_type;
	private Integer i_trackNumber;
	
	@Override
	public int compareTo(FlacTrackBean o) {
		return getUrl().compareTo(o.getUrl());
	}
	
	@Transient
	public File getFile() {
		String url = getUrl();
		if (url.startsWith(s_urlPrefix)) {
			url = url.substring(s_urlPrefixLength);
		}
		return new File(url);
	}
	
	@Transient
	public String getTitle() {
		return new String(getRawTitle());
	}
	@Override
	@Id
	public Integer getId() {
		return super.getId();
	}

	@Override
	@Column(name="titlesort")
	@Lob
	public String getCode() {
		return super.getCode();
	}
	
	@Override
	public String toString() {
		return getUrl();
	}
	
	@ManyToOne
	@JoinColumn(name="album")
	public FlacAlbumBean getFlacAlbumBean() {
		return i_flacAlbumBean;
	}

	public void setFlacAlbumBean(FlacAlbumBean flacAlbumBean) {
		i_flacAlbumBean = flacAlbumBean;
	}

	public Long getTimestamp() {
		return i_timestamp;
	}

	public void setTimestamp(Long timestamp) {
		i_timestamp = timestamp;
	}

	@Lob
	@Column(name="title")
	public byte[] getRawTitle() {
		return i_rawTitle;
	}

	public void setRawTitle(byte[] rawTitle) {
		i_rawTitle = rawTitle;
	}

	public String getUrl() {
		return i_url;
	}

	public void setUrl(String url) {
		i_url = url;
	}

	@Column(name="content_type")
	public String getType() {
		return i_type;
	}

	public void setType(String type) {
		i_type = type;
	}

	@Column(name="tracknum")
	public Integer getTrackNumber() {
		return i_trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		i_trackNumber = trackNumber;
	}


}
