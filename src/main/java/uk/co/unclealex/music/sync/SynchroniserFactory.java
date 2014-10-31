package uk.co.unclealex.music.sync;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;

/**
 * A factory interface for creating {@link Synchroniser}s for {@link Device}s.
 * 
 * @author alex
 * 
 */
public interface SynchroniserFactory<D extends Device> {

  /**
   * Create a new synchroniser.
   * 
   * @param owner The {@link User} who owns the device.
   * @param device
   *          The {@link Device} to be synchronised.
   * @return A new synchroniser that can synchronise the supplied {@link Device}
   *         .
   */
  public Synchroniser createSynchroniser(User owner, D device);

}
