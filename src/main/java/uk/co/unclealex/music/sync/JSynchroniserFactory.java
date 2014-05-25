package uk.co.unclealex.music.sync;

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;

/**
 * A factory interface for creating {@link JSynchroniser}s for {@link uk.co.unclealex.music.configuration.JDevice}s.
 * 
 * @author alex
 * 
 */
public interface JSynchroniserFactory<D extends JDevice> {

  /**
   * Create a new synchroniser.
   * 
   * @param owner The {@link uk.co.unclealex.music.configuration.JUser} who owns the device.
   * @param device
   *          The {@link uk.co.unclealex.music.configuration.JDevice} to be synchronised.
   * @return A new synchroniser that can synchronise the supplied {@link uk.co.unclealex.music.configuration.JDevice}
   *         .
   */
  public JSynchroniser createSynchroniser(JUser owner, D device);

}
