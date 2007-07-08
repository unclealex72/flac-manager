package uk.co.unclealex.flacconverter.encoded.model;

import java.util.SortedSet;

import javax.persistence.CascadeType;
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
	private SortedSet<EncodedTrackBean> i_encodedTrackBeans;
	private SortedSet<DeviceBean> i_deviceBeans;

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return super.getId();
	}
	
	@Override
	public String toString() {
		return "[Encoder: " + getExtension() + "]";
	}
	
	@OneToMany(mappedBy="encoderBean", cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans() {
		return i_encodedTrackBeans;
	}

	public void setEncodedTrackBeans(SortedSet<EncodedTrackBean> encodedTrackBeans) {
		i_encodedTrackBeans = encodedTrackBeans;
	}

	@NotEmpty(message="You must supply a command.")
	@Length(max=255,message="Please use a shorter command.")
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
}
