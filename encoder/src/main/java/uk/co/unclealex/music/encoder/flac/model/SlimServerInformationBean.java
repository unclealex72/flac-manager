package uk.co.unclealex.music.encoder.flac.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@org.hibernate.annotations.Entity(mutable=false)
@Table(name="metainformation")
public class SlimServerInformationBean {

	private String i_name;
	private Long i_value;
	
	@Id
	public String getName() {
		return i_name;
	}
	public void setName(String name) {
		i_name = name;
	}
	
	public Long getValue() {
		return i_value;
	}
	public void setValue(Long value) {
		i_value = value;
	}
}
