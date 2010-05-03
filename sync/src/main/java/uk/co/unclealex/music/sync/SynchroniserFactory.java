package uk.co.unclealex.music.sync;

import uk.co.unclealex.music.Device;

public interface SynchroniserFactory {

	public Synchroniser createSynchroniser(Device device);

}
