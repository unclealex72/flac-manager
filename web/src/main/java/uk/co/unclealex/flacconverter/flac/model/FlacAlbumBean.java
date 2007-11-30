package uk.co.unclealex.flacconverter.flac.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;

@Entity
@org.hibernate.annotations.Entity(mutable=false)
@Table(name="albums")
public class FlacAlbumBean extends AbstractFlacBean<FlacAlbumBean> {

	private SortedSet<FlacTrackBean> i_flacTrackBeans;
	private FlacArtistBean i_flacArtistBean;
	private byte[] i_rawTitle;

	@Override
	public void accept(FlacVisitor flacVisitor) {
		flacVisitor.visit(this);
	}

	@Override
	public String toString() {
		return "[" + getFlacArtistBean().getName() + ": " + getTitle() + "]";
	}

	@Override
	@Id
	public Integer getId() {
		return super.getId();
	}

	@Override
	@Column(name="titlesort")
	@Lob
	public String getCode() {
		return super.getCode();
	}

	@Transient
	public String getTitle() {
		return new String(getRawTitle());
	}
	
	@Override
	public int compareTo(FlacAlbumBean o) {
		int cmp = getFlacArtistBean().compareTo(o.getFlacArtistBean());
		return cmp==0?super.compareTo(o):cmp;
	}
	
	@ManyToOne
	@JoinColumn(name="contributor")
	public FlacArtistBean getFlacArtistBean() {
		return i_flacArtistBean;
	}

	public void setFlacArtistBean(FlacArtistBean flacArtistBean) {
		i_flacArtistBean = flacArtistBean;
	}

	@OneToMany(mappedBy="flacAlbumBean", targetEntity=FlacTrackBean.class)
	@Sort(type=SortType.NATURAL)
	public SortedSet<FlacTrackBean> getFlacTrackBeans() {
		return i_flacTrackBeans;
	}

	public void setFlacTrackBeans(SortedSet<FlacTrackBean> flacTrackBeans) {
		i_flacTrackBeans = flacTrackBeans;
	}

	@Lob
	@Column(name="title")
	public byte[] getRawTitle() {
		return i_rawTitle;
	}

	public void setRawTitle(byte[] rawTitle) {
		i_rawTitle = rawTitle;
	}

}
