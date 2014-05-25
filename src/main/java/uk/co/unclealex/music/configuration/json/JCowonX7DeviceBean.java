package uk.co.unclealex.music.configuration.json;

import uk.co.unclealex.music.configuration.JCowonX7Device;
import uk.co.unclealex.music.configuration.JDeviceVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link uk.co.unclealex.music.configuration.JCowonX7Device}.
 * 
 * @author alex
 * 
 */
@JsonIgnoreProperties("name")
public class JCowonX7DeviceBean extends JAbstractFileSystemDeviceBean implements JCowonX7Device {

  @JsonCreator
  public JCowonX7DeviceBean(@JsonProperty("uuid") final String uuid) {
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
  public <R> R accept(final JDeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit(this);
  }
}
