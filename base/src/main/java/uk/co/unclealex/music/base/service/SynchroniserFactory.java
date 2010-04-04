package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.DeviceBean;

public interface SynchroniserFactory {

	public <D extends DeviceBean, C extends ConnectedDevice<D>> Synchroniser<D, C> createSynchroniser(
			C connectedDevice, SynchroniserCallback synchroniserCallback);

}
