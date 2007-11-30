package uk.co.unclealex.flacconverter.flac.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;

@Entity
@org.hibernate.annotations.Entity(mutable=false)
@Table(name="contributors")
public class FlacArtistBean extends AbstractFlacBean<FlacArtistBean> {

	private byte[] i_rawName;
	private SortedSet<FlacAlbumBean> i_flacAlbumBeans;
	
	@Override
	public void accept(FlacVisitor flacVisitor) {
		flacVisitor.visit(this);
	}

	@Override
	public String toString() {
		return "[" + getName() + "]";
	}

	@Override
	@Id
	public Integer getId() {
		return super.getId();
	}

	@Transient
	public String getName() {
		return new String(getRawName());
	}
	
	@Override
	@Column(name="namesort")
	@Lob
	public String getCode() {
		return super.getCode();
	}

	@OneToMany(mappedBy="flacArtistBean", targetEntity=FlacAlbumBean.class)
	@Sort(type=SortType.NATURAL)
	public SortedSet<FlacAlbumBean> getFlacAlbumBeans() {
		return i_flacAlbumBeans;
	}

	public void setFlacAlbumBeans(SortedSet<FlacAlbumBean> flacAlbumBeans) {
		i_flacAlbumBeans = flacAlbumBeans;
	}

	@Lob
	@Column(name="name")
	public byte[] getRawName() {
		return i_rawName;
	}

	public void setRawName(byte[] rawName) {
		i_rawName = rawName;
	}
}
