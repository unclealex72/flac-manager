package uk.co.unclealex.music.base.service;

public interface DeviceSyncService {

	public void synchronise(ConnectedDevice<?> connectedDevice, SynchroniserCallback synchroniserCallback) throws SynchronisationException;
}
