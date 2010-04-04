package uk.co.unclealex.music.base.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import uk.co.unclealex.music.base.visitor.DeviceVisitor;

@Entity(name="ipodDeviceBean")
@DiscriminatorValue("ipod")
public class IpodDeviceBean extends MountPointDeviceBean {

	public <R, E extends Exception> R accept(DeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
	
}
