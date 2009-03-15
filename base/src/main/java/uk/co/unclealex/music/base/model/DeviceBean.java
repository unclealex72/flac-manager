package uk.co.unclealex.music.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;

import uk.co.unclealex.hibernate.model.KeyedBean;

@Entity
@Table(name="devices")
public class DeviceBean extends KeyedBean<DeviceBean> {

	private String i_identifier;
	private String i_description;
	private String i_titleFormat;
	private Boolean i_deletingRequired;
	private OwnerBean i_ownerBean;
	private EncoderBean i_encoderBean;	
	private SpeechProviderEnum i_speechProviderEnum;
	
	public DeviceBean() {
		// Auto-generated constructor stub
	}
	
	public DeviceBean(String identifier, String description,
			String titleFormat, Boolean deletingRequired, OwnerBean ownerBean,
			EncoderBean encoderBean) {
		super();
		i_identifier = identifier;
		i_description = description;
		i_titleFormat = titleFormat;
		i_deletingRequired = deletingRequired;
		i_ownerBean = ownerBean;
		i_encoderBean = encoderBean;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}

	@Transient
	public String getFullDescription() {
		return getOwnerBean().getName() + "'s " + getDescription();
	}
	
	@Override
	public String toString() {
		return "Device: " + getFullDescription();
	}
	
	@Column(unique=true)
	@NotNull
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
	
	@NotNull
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
	@NotNull
	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}

	@NotNull
	public Boolean isDeletingRequired() {
		return i_deletingRequired;
	}

	public void setDeletingRequired(Boolean requiresDeleting) {
		i_deletingRequired = requiresDeleting;
	}

	@Enumerated(EnumType.STRING)
	@NotNull
	public SpeechProviderEnum getSpeechProviderEnum() {
		return i_speechProviderEnum;
	}

	public void setSpeechProviderEnum(SpeechProviderEnum speechProviderEnum) {
		i_speechProviderEnum = speechProviderEnum;
	}
	
	
}
