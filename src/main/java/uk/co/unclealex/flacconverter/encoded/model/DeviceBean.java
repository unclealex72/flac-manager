package uk.co.unclealex.flacconverter.encoded.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="devices")
public class DeviceBean extends KeyedBean<DeviceBean> {

	private String i_identifier;
	private String i_description;
	private String i_titleFormat;
	private OwnerBean i_ownerBean;
	private EncoderBean i_encoderBean;	
	
	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Column(unique=true)
	public String getIdentifier() {
		return i_identifier;
	}
	
	public void setIdentifier(String identifier) {
		i_identifier = identifier;
	}
	
	@ManyToOne
	public OwnerBean getOwnerBean() {
		return i_ownerBean;
	}
	
	public void setOwnerBean(OwnerBean ownerBean) {
		i_ownerBean = ownerBean;
	}
	
	public String getDescription() {
		return i_description;
	}
	public void setDescription(String description) {
		i_description = description;
	}

	@ManyToOne
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}

	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}

	@Lob
	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}
	
	
}
