package uk.co.unclealex.music.configuration;

import java.util.List;

/**
 * The main interface for holding configuration information about where files are stored as well as for users and
 * their devices.
 * 
 * @author alex
 *
 */
public interface Configuration {

  /**
   * Get the directories where the different types of files are stored.
   * @return A {@link Directories} object containing the directories where files are stored.
   */
  public Directories getDirectories();

  /**
   * Get a list of all the known {@link User}s.
   * @return A list of all the known {@link User}s.
   */
  public List<? extends User> getUsers();
  
  /**
   * Get the configuration object required to talk to Amazon web services.
   * @return the configuration object required to talk to Amazon web services.
   */
  public AmazonConfiguration getAmazon();
}
