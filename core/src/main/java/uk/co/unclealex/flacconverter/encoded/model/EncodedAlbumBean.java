package uk.co.unclealex.flacconverter.encoded.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

@Table(name="encoded_albums")
@Entity(name="encodedAlbumBean")
public class EncodedAlbumBean extends KeyedBean<EncodedAlbumBean> {

	private String i_title;
	private SortedSet<EncodedTrackBean> i_encodedTrackBeans;
	private EncodedArtistBean i_encodedArtistBean;
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return getTitle();
	}
	
	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}

	@OneToMany(mappedBy="encodedAlbumBean", targetEntity=EncodedTrackBean.class)
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans() {
		return i_encodedTrackBeans;
	}

	public void setEncodedTrackBeans(SortedSet<EncodedTrackBean> encodedTrackBeans) {
		i_encodedTrackBeans = encodedTrackBeans;
	}

	@ManyToOne
	public EncodedArtistBean getEncodedArtistBean() {
		return i_encodedArtistBean;
	}

	public void setEncodedArtistBean(EncodedArtistBean encodedArtistBean) {
		i_encodedArtistBean = encodedArtistBean;
	}


}
