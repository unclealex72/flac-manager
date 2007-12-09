package uk.co.unclealex.flacconverter.encoded.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

@Table(name="encoded_artists")
@Entity(name="encodedArtistBean")
public class EncodedArtistBean extends KeyedBean<EncodedArtistBean> {

	private String i_name;
	private SortedSet<EncodedAlbumBean> i_encodedAlbumBeans;
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return i_name;
	}
	public void setName(String name) {
		i_name = name;
	}
	@OneToMany(mappedBy="encodedArtistBean", targetEntity=EncodedAlbumBean.class)
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedAlbumBean> getEncodedAlbumBeans() {
		return i_encodedAlbumBeans;
	}
	public void setEncodedAlbumBeans(SortedSet<EncodedAlbumBean> encodedAlbumBeans) {
		i_encodedAlbumBeans = encodedAlbumBeans;
	}
	

}
