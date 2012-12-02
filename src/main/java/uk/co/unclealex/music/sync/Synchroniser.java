package uk.co.unclealex.music.sync;

import java.io.IOException;

import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Multimap;

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
   * @param deviceFilesByOwner
   *          A list of {@link FileLocation}s in the device repositories that
   *          are owned by a set of users that includes the owner of this
   *          device.
   * 
   * @throws IOException
   */
  public void synchronise(Multimap<User, FileLocation> deviceFilesByOwner) throws IOException;

}
