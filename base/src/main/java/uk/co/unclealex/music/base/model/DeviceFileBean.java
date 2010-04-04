package uk.co.unclealex.music.base.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.co.unclealex.hibernate.model.KeyedBean;

@Table(name="device_files")
@Entity(name="deviceFileBean")
public class DeviceFileBean extends KeyedBean<DeviceFileBean> implements Comparable<DeviceFileBean> {

	private String i_artistCode;
	private String i_albumCode;
	private String i_trackCode;
	private int i_trackNumber;
	private String i_deviceFileId;
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public int compareTo(DeviceFileBean o) {
		int cmp = getArtistCode().compareTo(o.getArtistCode());
		if (cmp == 0) {
			cmp = getAlbumCode().compareTo(getAlbumCode());
			if (cmp == 0) {
				cmp = getTrackNumber() - o.getTrackNumber();
				if (cmp == 0) {
					cmp = getTrackCode().compareTo(o.getTrackCode());
					if (cmp == 0) {
						cmp = super.compareTo(o);
					}
				}
			}
		}
		return cmp;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s-%s-%d-%s->%s", getArtistCode(), getAlbumCode(), getTrackNumber(), getTrackCode(), getDeviceFileId());
	}

	public String getArtistCode() {
		return i_artistCode;
	}

	public void setArtistCode(String artistCode) {
		i_artistCode = artistCode;
	}

	public String getAlbumCode() {
		return i_albumCode;
	}

	public void setAlbumCode(String albumCode) {
		i_albumCode = albumCode;
	}

	public String getTrackCode() {
		return i_trackCode;
	}

	public void setTrackCode(String trackCode) {
		i_trackCode = trackCode;
	}

	public int getTrackNumber() {
		return i_trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		i_trackNumber = trackNumber;
	}

	public String getDeviceFileId() {
		return i_deviceFileId;
	}

	public void setDeviceFileId(String deviceFileId) {
		i_deviceFileId = deviceFileId;
	}

}
