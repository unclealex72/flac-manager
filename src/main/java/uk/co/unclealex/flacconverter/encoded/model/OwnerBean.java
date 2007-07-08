package uk.co.unclealex.flacconverter.encoded.model;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.acegi.User;

@Table(name="owners")
@Entity
public class OwnerBean extends KeyedBean<OwnerBean> implements User {

	private String i_name;
	private String i_passwordHash;
	
	private Boolean i_ownsAll;
	private SortedSet<OwnedAlbumBean> i_ownedAlbumBeans;
	private SortedSet<OwnedArtistBean> i_ownedArtistBeans;
	private SortedSet<DeviceBean> i_deviceBeans;
	
	@Override
	@Transient
	public String getUsername() {
		return getName();
	}
	
	@Override
	@Transient
	public String getPassword() {
		return getPasswordHash();
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
	public SortedSet<OwnedAlbumBean> getOwnedAlbumBeans() {
		return i_ownedAlbumBeans;
	}

	public void setOwnedAlbumBeans(SortedSet<OwnedAlbumBean> ownedAlbumBeans) {
		i_ownedAlbumBeans = ownedAlbumBeans;
	}

	@OneToMany(mappedBy="ownerBean", cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	public SortedSet<OwnedArtistBean> getOwnedArtistBeans() {
		return i_ownedArtistBeans;
	}

	public void setOwnedArtistBeans(SortedSet<OwnedArtistBean> ownedArtistBeans) {
		i_ownedArtistBeans = ownedArtistBeans;
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

	@Lob
	public String getPasswordHash() {
		return i_passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		i_passwordHash = passwordHash;
	}
}
