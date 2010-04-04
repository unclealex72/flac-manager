package uk.co.unclealex.music.base.service;

import java.io.File;
import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.DeviceFileBean;

public interface Synchroniser<D extends DeviceBean, C extends ConnectedDevice<D>> {

	public void postConstruct(C connectedDevice, SynchroniserCallback synchroniserCallback);
	
	public void initialise() throws SynchronisationException;

	public void synchronise(
			SortedSet<DeviceFileBean> deviceFilesToDelete, SortedMap<DeviceFileBean, File> deviceFilesToAdd) throws SynchronisationException;

	public void destroy();

}
