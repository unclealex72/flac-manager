package uk.co.unclealex.music.configuration.json;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import uk.co.unclealex.music.configuration.Directories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A configuration bean that is used to hold where the various directories are.
 * 
 * @author alex
 * 
 */
public class PathsBean implements Directories {

  /**
   * The top level path where FLAC files are stored.
   */
  @NotNull
  private final Path flacPath;

  /**
   * The top level path where symbolic links for devices are created.
   */
  @NotNull
  private final Path devicesPath;

  /**
   * The top level path where encoded files are stored.
   */
  @NotNull
  private final Path encodedPath;

  /**
   * The top level path where new and altered FLAC files are staged.
   */
  @NotNull
  private final Path stagingPath;

  /**
   * 
   * @param flacPath
   * @param devicesPath
   * @param encodedPath
   * @param stagingPath
   */
  @JsonCreator
  public PathsBean(
      @JsonProperty("flacPath") Path flacPath,
      @JsonProperty("devicesPath") Path devicesPath,
      @JsonProperty("encodedPath") Path encodedPath,
      @JsonProperty("stagingPath") Path stagingPath) {
    super();
    this.flacPath = flacPath;
    this.devicesPath = devicesPath;
    this.encodedPath = encodedPath;
    this.stagingPath = stagingPath;
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
  @Override
  public Path getFlacPath() {
    return flacPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getDevicesPath() {
    return devicesPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getEncodedPath() {
    return encodedPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getStagingPath() {
    return stagingPath;
  }

}
