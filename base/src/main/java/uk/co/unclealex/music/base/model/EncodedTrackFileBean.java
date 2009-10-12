package uk.co.unclealex.music.base.model;

import java.io.File;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Entity(name="encodedTrackFileBean")
@DiscriminatorValue("file")
public class EncodedTrackFileBean extends FileBean {

	private EncodedTrackBean i_encodedTrackBean;
	
	public <R, E extends Exception> R accept(DaoFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@Transient
	public File getFile() {
		return getEncodedTrackBean().getTrackDataBean().getFile();
	}

	@ManyToOne
	public EncodedTrackBean getEncodedTrackBean() {
		return i_encodedTrackBean;
	}

	public void setEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		i_encodedTrackBean = encodedTrackBean;
	}

}
