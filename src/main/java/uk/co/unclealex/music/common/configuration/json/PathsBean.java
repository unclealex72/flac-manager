package uk.co.unclealex.music.common.configuration.json;

import java.nio.file.Path;

import uk.co.unclealex.music.common.DataObject;
import uk.co.unclealex.music.common.configuration.Directories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A configuration bean that is used to hold where the various directories are.
 * 
 * @author alex
 * 
 */
public class PathsBean extends DataObject implements Directories {

  /**
   * The top level path where FLAC files are stored.
   */
  private final Path flacPath;

  /**
   * The top level path where symbolic links for devices are created.
   */
  private final Path devicesPath;

  /**
   * The top level path where encoded files are stored.
   */
  private final Path encodedPath;

  /**
   * The top level path where new and altered FLAC files are staged.
   */
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
