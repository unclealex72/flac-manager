package uk.co.unclealex.music.base.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.visitor.DeviceVisitor;

@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="deviceType", discriminatorType=DiscriminatorType.STRING)
@Table(name="devices")
@Entity(name="deviceBean")
public abstract class DeviceBean extends KeyedBean<DeviceBean> {

	private OwnerBean i_ownerBean;
	private EncoderBean i_encoderBean;
	private String i_deviceId;
	private String i_name;
	private SortedSet<DeviceFileBean> i_deviceFileBeans = new TreeSet<DeviceFileBean>();
	private Long i_lastSyncTimestamp;
	
	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	public <R, E extends Exception> R accept(DeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s' %s (%s)", getOwnerBean().getName(), getName(), getDeviceId());
	}

	@ManyToOne
	public OwnerBean getOwnerBean() {
		return i_ownerBean;
	}

	public void setOwnerBean(OwnerBean ownerBean) {
		i_ownerBean = ownerBean;
	}

	@ManyToOne
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}

	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}

	public String getDeviceId() {
		return i_deviceId;
	}

	public void setDeviceId(String deviceId) {
		i_deviceId = deviceId;
	}

	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@Sort(type=SortType.NATURAL)
	public SortedSet<DeviceFileBean> getDeviceFileBeans() {
		return i_deviceFileBeans;
	}

	public void setDeviceFileBeans(SortedSet<DeviceFileBean> deviceFileBeans) {
		i_deviceFileBeans = deviceFileBeans;
	}

	public Long getLastSyncTimestamp() {
		return i_lastSyncTimestamp;
	}

	public void setLastSyncTimestamp(Long lastSyncTimestamp) {
		i_lastSyncTimestamp = lastSyncTimestamp;
	}
	
}
