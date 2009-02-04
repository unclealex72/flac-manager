package uk.co.unclealex.music.core.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity(name="pictureBean")
@Table(name="pictures")
public class PictureBean extends KeyedBean<PictureBean> {

	private byte[] i_picture;
	
	public PictureBean() {
		// Default constructor.
	}
	
	public PictureBean(byte[] picture) {
		i_picture = picture;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return "Picture " + getId();
	}
	
	@Lob
	public byte[] getPicture() {
		return i_picture;
	}

	public void setPicture(byte[] picture) {
		i_picture = picture;
	}
}
