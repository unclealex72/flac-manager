package uk.co.unclealex.music.base.model;

import java.io.File;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import uk.co.unclealex.music.base.visitor.DaoAwareFileVisitor;
import uk.co.unclealex.music.base.visitor.FileVisitor;

@Entity(name="encodedTrackFileBean")
@DiscriminatorValue("file")
public class EncodedTrackFileBean extends AbstractFileBean implements DataFileBean {

	private EncodedTrackBean i_encodedTrackBean;
	
	@Override
	public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
		return fileVisitor.visit(EncodedTrackFileBean.this);
	}
	
	public <R, E extends Exception> R accept(DaoAwareFileVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@Override
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
