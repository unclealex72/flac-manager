package uk.co.unclealex.music.configuration;

/**
 * A visitor for {@link Device}s.
 * 
 * @author alex
 * 
 * @param <R>
 */
public interface DeviceVisitor<R> {

  /**
   * Visit a {@link Device}.
   * 
   * @param device
   *          The device to visit.
   * @return This method will always throw an exception.
   */
  public R visit(Device device);

  /**
   * Visit an {@link IpodDevice}.
   * 
   * @param ipodDevice
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(IpodDevice ipodDevice);

  /**
   * Visit a {@link FileSystemDevice}.
   * 
   * @param fileSystemDevice
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(FileSystemDevice fileSystemDevice);

  /**
   * Visit a {@link CowonX7Device}.
   * 
   * @param cowonX7Device
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(CowonX7Device cowonX7Device);

  /**
   * A default implementation of {@link DeviceVisitor} that throws an exception
   * if {@link DeviceVisitor#visit(Device)} is called.
   * 
   * @author alex
   * 
   * @param <R>
   */
  public abstract class Default<R> implements DeviceVisitor<R> {

    @Override
    public final R visit(final Device device) {
      throw new IllegalArgumentException(device.getClass() + " is not a valid device type.");
    }
  }
}
