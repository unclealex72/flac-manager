package uk.co.unclealex.music.core.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.core.visitor.EncodedVisitor;

@Table(name="encoded_albums")
@Entity(name="encodedAlbumBean")
public class EncodedAlbumBean extends IdentifiableBean<EncodedAlbumBean, String> implements EncodedBean {

	private String i_title;
	private SortedSet<EncodedTrackBean> i_encodedTrackBeans;
	private EncodedArtistBean i_encodedArtistBean;
	private SortedSet<OwnerBean> i_ownerBeans;
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	@NotNull
	public String getFilename() {
		return super.getFilename();
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	@Override
	public void accept(EncodedVisitor encodedVisitor) {
		encodedVisitor.visit(this);
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

	@ManyToMany(mappedBy="encodedAlbumBeans")
	@Sort(type=SortType.NATURAL)
	public SortedSet<OwnerBean> getOwnerBeans() {
		return i_ownerBeans;
	}

	public void setOwnerBeans(SortedSet<OwnerBean> ownerBeans) {
		i_ownerBeans = ownerBeans;
	}

	@Override
	@NotNull
	@Column(name="identifier")
	public String getIdentifier() {
		return super.getIdentifier();
	}

}
