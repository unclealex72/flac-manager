package uk.co.unclealex.music.configuration.json;

import java.nio.file.Path;

import uk.co.unclealex.music.configuration.JDeviceVisitor;
import uk.co.unclealex.music.configuration.JFileSystemDevice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link uk.co.unclealex.music.configuration.JFileSystemDevice}.
 * 
 * @author alex
 */
public class JFileSystemDeviceBean extends JAbstractFileSystemDeviceBean implements JFileSystemDevice {

  /**
   * The user friendly name of this device.
   */
  private final String name;

  /**
   * The path, relative to the mount pount, where music is stored, or null if
   * music is stored at the root level.
   */
  private final Path relativeMusicPath;

  @JsonCreator
  public JFileSystemDeviceBean(
          @JsonProperty("name") final String name,
          @JsonProperty("uuid") final String uuid,
          @JsonProperty("relativeMusicPath") final Path relativeMusicPath) {
    super(uuid);
    this.name = name;
    this.relativeMusicPath = relativeMusicPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R accept(final JDeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getRelativeMusicPath() {
    return relativeMusicPath;
  }
}
