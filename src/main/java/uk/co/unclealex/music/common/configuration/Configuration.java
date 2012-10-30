package uk.co.unclealex.music.common.configuration;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

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
  @NotNull
  @Valid
  public Directories getDirectories();

  /**
   * Get a list of all the known {@link User}s.
   * @return A list of all the known {@link User}s.
   */
  @NotEmpty
  @Valid
  public List<? extends User> getUsers();
  
  /**
   * Get the configuration object required to talk to Amazon web services.
   * @return the configuration object required to talk to Amazon web services.
   */
  @NotNull
  @Valid
  public AmazonConfiguration getAmazon();
}
