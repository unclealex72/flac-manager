package uk.co.unclealex.music.configuration.json;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.co.unclealex.music.configuration.DeviceVisitor;
import uk.co.unclealex.music.configuration.FileSystemDevice;

/**
 * A bean version of {@link FileSystemDevice}.
 *
 * @author alex
 */
public class FileSystemDeviceBean extends AbstractFileSystemDeviceBean implements FileSystemDevice {

  /**
   * The user friendly name of this device.
   */
  @NotNull
  private final String name;

  /**
   * The path, relative to the mount pount, where music is stored, or null if
   * music is stored at the root level.
   */
  private final Path relativeMusicPath;

  @JsonCreator  
  public FileSystemDeviceBean(@JsonProperty("name") String name, @JsonProperty("mountPoint") Path mountPoint, @JsonProperty("relativeMusicPath") Path relativeMusicPath) {
    super(mountPoint);
    this.name = name;
    this.relativeMusicPath = relativeMusicPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R accept(DeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit((FileSystemDevice) this);
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
