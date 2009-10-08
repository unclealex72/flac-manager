package uk.co.unclealex.music.base.model;

import java.util.Comparator;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.base.visitor.EncodedVisitor;
import uk.co.unclealex.music.base.visitor.VisitorException;

@Table(name="encoded_artists")
@Entity(name="encodedArtistBean")
public class EncodedArtistBean extends CodedBean<EncodedArtistBean> {

	protected static final Comparator<EncodedArtistBean> ENCODED_ARTIST_COMPARATOR =
		new Comparator<EncodedArtistBean>() {
			@Override
			public int compare(EncodedArtistBean o1, EncodedArtistBean o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
	};
	
	private String i_name;
	private SortedSet<EncodedAlbumBean> i_encodedAlbumBeans;
	
	@Override
	public int compareTo(EncodedArtistBean o) {
		return ENCODED_ARTIST_COMPARATOR.compare(this, o);
	}
	
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
		return getName();
	}

	@Override
	public <R, E extends Exception> R accept(EncodedVisitor<R, E> encodedVisitor) {
		try {
			return encodedVisitor.visit(this);
		}
		catch (Exception e) {
			throw new VisitorException(e);
		}
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

	@Override
	@NotNull
	@Column(name="code")
	public String getCode() {
		return super.getCode();
	}

}
