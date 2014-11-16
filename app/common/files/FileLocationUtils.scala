package common.files

import common.configuration.Directories

/**
 * A trait that wraps Path like functionality for FileLocations
 * Created by alex on 16/11/14.
 */
trait FileLocationUtils {

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation
   * @return
   */
  protected[files] def isDirectory(fileLocation: FileLocation): Boolean

  protected[files] def exists(fileLocation: FileLocation): Boolean

  protected[files] def createTemporaryFileLocation()(implicit directories: Directories): TemporaryFileLocation

  protected[files] def lastModified(fileLocation: FileLocation): Long
}
