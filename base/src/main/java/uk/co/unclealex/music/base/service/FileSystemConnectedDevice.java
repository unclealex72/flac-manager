package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.FileSystemDeviceBean;
import uk.co.unclealex.music.base.visitor.ConnectedDeviceVisitor;

public class FileSystemConnectedDevice extends ConnectedDevice<FileSystemDeviceBean> {
	
	public FileSystemConnectedDevice(FileSystemDeviceBean deviceBean) {
		super(deviceBean);
	}

	public <R, E extends Exception> R accept(ConnectedDeviceVisitor<R, E> visitor) {
		return visitor.visit(this);
	}
}
