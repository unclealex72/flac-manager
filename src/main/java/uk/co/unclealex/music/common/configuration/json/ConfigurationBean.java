package uk.co.unclealex.music.common.configuration.json;

import java.util.List;

import uk.co.unclealex.music.common.DataObject;
import uk.co.unclealex.music.common.configuration.AmazonConfiguration;
import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.Directories;
import uk.co.unclealex.music.common.configuration.User;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The main interface for holding configuration information about where files are stored as well as for users and
 * their devices.
 * 
 * @author alex
 *
 */
public class ConfigurationBean extends DataObject implements Configuration {

  /**
   * A {@link Directories} object containing the directories where files are stored.
   */
  private final PathsBean directories;

  /**
   * A list of all the known {@link User}s.
   */
  private final List<UserBean> users;

  /**
   * The {@link AmazonConfiguration} used to talk to Amazon.
   */
  private final AmazonConfigurationBean amazon;
  
  /**
   * Instantiates a new configuration bean.
   *
   * @param directories the directories
   * @param users the users
   * @param amazon the amazon configuration bean
   */
  @JsonCreator
  public ConfigurationBean(@JsonProperty("directories") PathsBean directories, @JsonProperty("users") List<UserBean> users, @JsonProperty("amazon") AmazonConfigurationBean amazonConfigurationBean) {
    super();
    this.directories = directories;
    this.users = users;
    this.amazon = amazonConfigurationBean;
  }

  /**
   * {@inheritDoc}
   */
  public PathsBean getDirectories() {
    return directories;
  }

  /**
   * {@inheritDoc}
   */
  public List<UserBean> getUsers() {
    return users;
  }

  /**
   * {@inheritDoc}
   */
  public AmazonConfigurationBean getAmazon() {
    return amazon;
  }
}
