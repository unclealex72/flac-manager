package uk.co.unclealex.music.base.service;

import java.io.IOException;
import java.util.SortedSet;

public interface ConnectedDeviceFactory {

	public SortedSet<ConnectedDevice<?>> findAllConnectedDevices() throws IOException;
}
