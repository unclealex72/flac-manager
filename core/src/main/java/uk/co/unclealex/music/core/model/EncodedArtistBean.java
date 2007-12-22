package uk.co.unclealex.music.core.model;

import java.util.SortedSet;

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
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.core.visitor.EncodedVisitor;

@Table(name="encoded_artists")
@Entity(name="encodedArtistBean")
public class EncodedArtistBean extends IdentifiableBean<EncodedArtistBean, String> implements EncodedBean {

	private String i_name;
	private SortedSet<EncodedAlbumBean> i_encodedAlbumBeans;
	private SortedSet<OwnerBean> i_ownerBeans;
	
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

	@Override
	public void accept(EncodedVisitor encodedVisitor) {
		encodedVisitor.visit(this);
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

	@ManyToMany(mappedBy="encodedArtistBeans")
	@Sort(type=SortType.NATURAL)
	public SortedSet<OwnerBean> getOwnerBeans() {
		return i_ownerBeans;
	}

	public void setOwnerBeans(SortedSet<OwnerBean> ownerBeans) {
		i_ownerBeans = ownerBeans;
	}

	@Override
	@NotNull
	@Column(unique=true)
	public String getIdentifier() {
		return super.getIdentifier();
	}
}
