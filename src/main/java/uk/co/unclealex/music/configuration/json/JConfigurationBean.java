package uk.co.unclealex.music.configuration.json;

import java.util.List;

import uk.co.unclealex.music.JDataObject;
import uk.co.unclealex.music.configuration.AmazonConfiguration;
import uk.co.unclealex.music.configuration.JConfiguration;
import uk.co.unclealex.music.configuration.JUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The main interface for holding configuration information about where files are stored as well as for users and
 * their devices.
 * 
 * @author alex
 *
 */
public class JConfigurationBean extends JDataObject implements JConfiguration {

  /**
   * A {@link uk.co.unclealex.music.configuration.JDirectories} object containing the directories where files are stored.
   */
  private final JPathsBean directories;

  /**
   * A list of all the known {@link uk.co.unclealex.music.configuration.JUser}s.
   */
  private final List<JUser> users;

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
  public JConfigurationBean(@JsonProperty("directories") JPathsBean directories, @JsonProperty("users") List<JUserBean> users, @JsonProperty("amazon") AmazonConfigurationBean amazonConfigurationBean) {
    super();
    this.directories = directories;
    this.users = users == null ? null : Lists.newArrayList(Iterables.filter(users, JUser.class));
    this.amazon = amazonConfigurationBean;
  }

  /**
   * {@inheritDoc}
   */
  public JPathsBean getDirectories() {
    return directories;
  }

  /**
   * {@inheritDoc}
   */
  public List<JUser> getUsers() {
    return users;
  }

  /**
   * {@inheritDoc}
   */
  public AmazonConfigurationBean getAmazon() {
    return amazon;
  }
}
