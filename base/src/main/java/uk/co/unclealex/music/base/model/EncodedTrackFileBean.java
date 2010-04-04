package uk.co.unclealex.music.base.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Entity(name="encodedTrackFileBean")
@DiscriminatorValue("file")
public class EncodedTrackFileBean extends FileBean {

	private EncodedTrackBean i_encodedTrackBean;
	private OwnerBean i_ownerBean;
	
	public <R, E extends Exception> R accept(DaoFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@ManyToOne
	public EncodedTrackBean getEncodedTrackBean() {
		return i_encodedTrackBean;
	}

	public void setEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		i_encodedTrackBean = encodedTrackBean;
	}

	@ManyToOne
	public OwnerBean getOwnerBean() {
		return i_ownerBean;
	}

	public void setOwnerBean(OwnerBean ownerBean) {
		i_ownerBean = ownerBean;
	}

}
