package uk.co.unclealex.music.configuration.json;

import uk.co.unclealex.music.configuration.CowonX7Device;
import uk.co.unclealex.music.configuration.DeviceVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link CowonX7Device}.
 * 
 * @author alex
 * 
 */
@JsonIgnoreProperties("name")
public class CowonX7DeviceBean extends AbstractFileSystemDeviceBean implements CowonX7Device {

  @JsonCreator
  public CowonX7DeviceBean(@JsonProperty("uuid") final String uuid) {
    super(uuid);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "Cowon X7";
  }

  @Override
  public <R> R accept(final DeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit(this);
  }
}
