package uk.co.unclealex.music.sync;

/**
 * A class that encapsulates a path of a file on an iPOD.
 * @author alex
 *
 */
public class DeviceFile implements Comparable<DeviceFile> {

  /**
   * The ID of the file on the iPOD.
   */
	private final String id;
	
	/**
	 * The relative path of the file on the iPOD.
	 */
	private final String relativePath;
	
	/**
	 * The time the file was last modified.
	 */
	private final long lastModified;

  /**
   * Instantiates a new device file.
   *
   * @param id the id
   * @param relativePath the relative path
   * @param lastModified the last modified
   */
  public DeviceFile(String id, String relativePath, long lastModified) {
    super();
    this.id = id;
    this.relativePath = relativePath;
    this.lastModified = lastModified;
  }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(DeviceFile o) {
	  int cmp = getRelativePath().compareTo(o.getRelativePath());
	  if (cmp == 0) {
	    cmp = getId().compareTo(o.getId());
	    if (cmp == 0) {
	      cmp = Long.valueOf(getLastModified()).compareTo(o.getLastModified());
	    }
	  }
	  return cmp;
	}

  /**
   * Gets the ID of the file on the iPOD.
   *
   * @return the ID of the file on the iPOD
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the relative path of the file on the iPOD.
   *
   * @return the relative path of the file on the iPOD
   */
  public String getRelativePath() {
    return relativePath;
  }

  /**
   * Gets the time the file was last modified.
   *
   * @return the time the file was last modified
   */
  public long getLastModified() {
    return lastModified;
  }
	
	
}
