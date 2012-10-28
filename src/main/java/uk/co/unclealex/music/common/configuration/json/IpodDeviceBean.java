package uk.co.unclealex.music.common.configuration.json;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.co.unclealex.music.common.configuration.DeviceVisitor;
import uk.co.unclealex.music.common.configuration.IpodDevice;


/**
 * A bean version of {@link IpodDevice}.
 * @author alex
 *
 */
@JsonIgnoreProperties("name")
public class IpodDeviceBean extends AbstractFileSystemDeviceBean implements IpodDevice {
  
  @JsonCreator
  public IpodDeviceBean(@JsonProperty("mountPoint") Path mountPoint) {
    super(mountPoint);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "iPOD";
  }
  
	@Override
	public <R> R accept(DeviceVisitor<R> deviceVisitor) {
		return deviceVisitor.visit((IpodDevice) this);
	}
}
