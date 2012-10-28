package uk.co.unclealex.music.configuration.json;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.Directories;
import uk.co.unclealex.music.configuration.User;

/**
 * The main interface for holding configuration information about where files are stored as well as for users and
 * their devices.
 * 
 * @author alex
 *
 */
public class ConfigurationBean implements Configuration {

  /**
   * A {@link Directories} object containing the directories where files are stored.
   */
  @NotNull
  @Valid
  private final PathsBean directories;

  /**
   * A list of all the known {@link User}s.
   */
  @NotEmpty
  @Valid
  private final List<UserBean> users;

  /**
   * 
   * @param directories
   * @param users
   */
  @JsonCreator
  public ConfigurationBean(@JsonProperty("directories") PathsBean directories, @JsonProperty("users") List<UserBean> users) {
    super();
    this.directories = directories;
    this.users = users;
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
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  /**
   * {@inheritDoc}
   */
  public List<UserBean> getUsers() {
    return users;
  }
}
