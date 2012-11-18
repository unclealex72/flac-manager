package uk.co.unclealex.music.sync;

import java.io.IOException;

/**
 * An interface for classes that can synchronise a music device with the device
 * repository. Note that instances of this interface are expected to be very
 * stateful with the device being synchronised being part of their state.
 * 
 * @author alex
 * 
 */
public interface Synchroniser {

  /**
   * Synchronise a device.
   * 
   * @throws IOException
   */
  public void synchronise() throws IOException;

}
