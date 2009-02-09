package uk.co.unclealex.music.base.model;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Table(name="encoders")
@Entity
public class EncoderBean extends KeyedBean<EncoderBean> {

	private String i_extension;
	private String i_command;
	private String i_magicNumber;
	private String i_contentType;
	private SortedSet<EncodedTrackBean> i_encodedTrackBeans;
	private SortedSet<DeviceBean> i_deviceBeans;

	public EncoderBean() {
		// Auto-generated constructor stub
	}
	
	public EncoderBean(String extension, String command, String magicNumber, String contentType) {
		super();
		i_extension = extension;
		i_command = command;
		i_magicNumber = magicNumber;
		i_contentType = contentType;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}
	
	@Override
	public String toString() {
		return getExtension();
	}
	
	@OneToMany(mappedBy="encoderBean", cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans() {
		return i_encodedTrackBeans;
	}

	public void setEncodedTrackBeans(SortedSet<EncodedTrackBean> encodedTrackBeans) {
		i_encodedTrackBeans = encodedTrackBeans;
	}

	@Column(length=1024)
	public String getCommand() {
		return i_command;
	}
	public void setCommand(String command) {
		i_command = command;
	}
	
	@NotEmpty(message="You must supply an extension.")
	@Length(max=50, message="Please use a shorter extension.")
	public String getExtension() {
		return i_extension;
	}
	public void setExtension(String extension) {
		i_extension = extension;
	}

	@OneToMany(mappedBy="encoderBean", cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	public SortedSet<DeviceBean> getDeviceBeans() {
		return i_deviceBeans;
	}

	public void setDeviceBeans(SortedSet<DeviceBean> deviceBeans) {
		i_deviceBeans = deviceBeans;
	}

	@NotEmpty(message="You must supply a magic number.")
	public String getMagicNumber() {
		return i_magicNumber;
	}

	public void setMagicNumber(String magicNumber) {
		i_magicNumber = magicNumber;
	}

	@NotEmpty(message="You must supply a content type.")
	public String getContentType() {
		return i_contentType;
	}

	public void setContentType(String contentType) {
		i_contentType = contentType;
	}
}
