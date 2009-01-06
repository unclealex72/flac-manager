package uk.co.unclealex.music.core.model;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Table(name="owners")
@Entity
public class OwnerBean extends KeyedBean<OwnerBean> {

	private String i_name;
	
	private Boolean i_ownsAll;
	private SortedSet<EncodedArtistBean> i_encodedArtistBeans;
	private SortedSet<EncodedAlbumBean> i_encodedAlbumBeans;
	private SortedSet<DeviceBean> i_deviceBeans;
	
	@Override
	public String toString() {
		return getName();
	}
	@Override
	public int compareTo(OwnerBean o) {
		return getName().compareTo(o.getName());
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@NotEmpty(message="An owner must have a name.")
	@Length(max=50, message="Please use a shorter name.")
	@Column(unique=true)
	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}

	@OneToMany(mappedBy="ownerBean", cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	public SortedSet<DeviceBean> getDeviceBeans() {
		return i_deviceBeans;
	}

	public void setDeviceBeans(SortedSet<DeviceBean> deviceBeans) {
		i_deviceBeans = deviceBeans;
	}

	@NotNull(message="You must state whether this owner owns all the albums or not.")
	public Boolean isOwnsAll() {
		return i_ownsAll;
	}

	public void setOwnsAll(Boolean ownsAll) {
		i_ownsAll = ownsAll;
	}

	@ManyToMany(cascade=CascadeType.PERSIST)
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedArtistBean> getEncodedArtistBeans() {
		return i_encodedArtistBeans;
	}

	public void setEncodedArtistBeans(
			SortedSet<EncodedArtistBean> encodedArtistBeans) {
		i_encodedArtistBeans = encodedArtistBeans;
	}

	@ManyToMany(cascade=CascadeType.PERSIST)
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedAlbumBean> getEncodedAlbumBeans() {
		return i_encodedAlbumBeans;
	}

	public void setEncodedAlbumBeans(SortedSet<EncodedAlbumBean> encodedAlbumBeans) {
		i_encodedAlbumBeans = encodedAlbumBeans;
	}
}
