package uk.co.unclealex.music.sync;

import java.io.IOException;

import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.files.JFileLocation;

import com.google.common.collect.Multimap;

/**
 * An interface for classes that can synchronise a music device with the device
 * repository. Note that instances of this interface are expected to be very
 * stateful with the device being synchronised being part of their state.
 * 
 * @author alex
 * 
 */
public interface JSynchroniser {

  /**
   * Synchronise a device.
   * 
   * @param deviceFilesByOwner
   *          A list of {@link uk.co.unclealex.music.files.JFileLocation}s in the device repositories that
   *          are owned by a set of users that includes the owner of this
   *          device.
   * 
   * @throws IOException
   */
  public void synchronise(Multimap<JUser, JFileLocation> deviceFilesByOwner) throws IOException;

}
