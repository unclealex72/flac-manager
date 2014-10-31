package uk.co.unclealex.music.configuration.json;

import uk.co.unclealex.music.configuration.DeviceVisitor;
import uk.co.unclealex.music.configuration.IpodDevice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A bean version of {@link IpodDevice}.
 * 
 * @author alex
 * 
 */
@JsonIgnoreProperties("name")
public class IpodDeviceBean extends AbstractFileSystemDeviceBean implements IpodDevice {

  @JsonCreator
  public IpodDeviceBean(@JsonProperty("uuid") final String uuid) {
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
  public <R> R accept(final DeviceVisitor<R> deviceVisitor) {
    return deviceVisitor.visit(this);
  }
}
