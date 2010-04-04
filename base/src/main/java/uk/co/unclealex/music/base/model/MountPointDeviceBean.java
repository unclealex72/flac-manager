package uk.co.unclealex.music.base.model;

public abstract class MountPointDeviceBean extends DeviceBean {

	private String i_uuid;

	public String getUuid() {
		return i_uuid;
	}

	public void setUuid(String uuid) {
		i_uuid = uuid;
	}
	
}
