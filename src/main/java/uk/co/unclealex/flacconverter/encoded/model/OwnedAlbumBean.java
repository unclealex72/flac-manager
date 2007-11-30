package uk.co.unclealex.flacconverter.encoded.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

@Table(name="owned_albums")
@Entity
public class OwnedAlbumBean extends KeyedBean<OwnedAlbumBean> {

	private OwnerBean i_ownerBean;
	private String i_albumName;
	private String i_artistName;
	
	@Override
	public int compareTo(OwnedAlbumBean o) {
		int cmp = getOwnerBean().compareTo(o.getOwnerBean());
		if (cmp != 0) { return cmp; }
		cmp = getArtistName().compareTo(o.getArtistName());
		if (cmp != 0) { return cmp; }
		cmp = getAlbumName().compareTo(o.getAlbumName());
		return cmp;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return "(" + getOwnerBean().getName() + ":" + getArtistName() + "," + getAlbumName() + ")";
	}

	@ManyToOne
	public OwnerBean getOwnerBean() {
		return i_ownerBean;
	}

	public void setOwnerBean(OwnerBean ownerBean) {
		i_ownerBean = ownerBean;
	}

	@NotEmpty(message="You must supply an album name.")
	@Lob
	public String getAlbumName() {
		return i_albumName;
	}

	public void setAlbumName(String albumName) {
		i_albumName = albumName;
	}

	public String getArtistName() {
		return i_artistName;
	}

	public void setArtistName(String artistName) {
		i_artistName = artistName;
	}
}
