package uk.co.unclealex.music.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

import uk.co.unclealex.music.base.io.KnownLengthInputStream;

@Entity(name="knownLengthInputStreamBean")
public class KnownLengthInputStreamBean extends KeyedBean<KnownLengthInputStreamBean> {

	private KnownLengthInputStream i_data;
	
	public KnownLengthInputStreamBean() {
		// Default constructor.
	}
	
	public KnownLengthInputStreamBean(KnownLengthInputStream data) {
		i_data = data;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return getClass() + ": " + getId();
	}

	@Lob
	@NotNull
	@Type(type="uk.co.unclealex.music.base.io.BlobUserType")
	@Columns(columns = {
	    @Column(name="data", length=Integer.MAX_VALUE),
	    @Column(name="dataLength")
	})
	public KnownLengthInputStream getData() {
		return i_data;
	}

	public void setData(KnownLengthInputStream data) {
		i_data = data;
	}
}
