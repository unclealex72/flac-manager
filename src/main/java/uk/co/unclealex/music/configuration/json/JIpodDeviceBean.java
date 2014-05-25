package uk.co.unclealex.music.configuration.json;

import uk.co.unclealex.music.configuration.JDeviceVisitor;
import uk.co.unclealex.music.configuration.JIpodDevice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link uk.co.unclealex.music.configuration.JIpodDevice}.
 * 
 * @author alex
 * 
 */
@JsonIgnoreProperties("name")
public class JIpodDeviceBean extends JAbstractFileSystemDeviceBean implements JIpodDevice {

  @JsonCreator
  public JIpodDeviceBean(@JsonProperty("uuid") final String uuid) {
    super(uuid);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "iPOD";
  }

  @Override
  public <R> R accept(final JDeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit(this);
  }
}
