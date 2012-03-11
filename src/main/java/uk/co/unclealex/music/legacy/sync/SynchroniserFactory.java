package uk.co.unclealex.music.legacy.sync;

import uk.co.unclealex.music.legacy.Device;

public interface SynchroniserFactory {

	public Synchroniser createSynchroniser(Device device);

}
