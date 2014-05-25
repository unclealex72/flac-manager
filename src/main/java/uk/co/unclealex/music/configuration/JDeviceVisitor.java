package uk.co.unclealex.music.configuration;

/**
 * A visitor for {@link JDevice}s.
 * 
 * @author alex
 * 
 * @param <R>
 */
public interface JDeviceVisitor<R> {

  /**
   * Visit a {@link JDevice}.
   * 
   * @param device
   *          The device to visit.
   * @return This method will always throw an exception.
   */
  public R visit(JDevice device);

  /**
   * Visit an {@link JIpodDevice}.
   * 
   * @param ipodDevice
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(JIpodDevice ipodDevice);

  /**
   * Visit a {@link JFileSystemDevice}.
   * 
   * @param fileSystemDevice
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(JFileSystemDevice fileSystemDevice);

  /**
   * Visit a {@link JCowonX7Device}.
   * 
   * @param cowonX7Device
   *          The device to visit.
   * @return A value to be defined by implementing classes.
   */
  public R visit(JCowonX7Device cowonX7Device);

  /**
   * A default implementation of {@link JDeviceVisitor} that throws an exception
   * if {@link JDeviceVisitor#visit(JDevice)} is called.
   * 
   * @author alex
   * 
   * @param <R>
   */
  public abstract class Default<R> implements JDeviceVisitor<R> {

    @Override
    public final R visit(final JDevice device) {
      throw new IllegalArgumentException(device.getClass() + " is not a valid device type.");
    }
  }
}
