package common.files

import common.configuration.Directories

/**
 * Created by alex on 16/11/14.
 */
trait TestFileLocationUtils extends FileLocationUtils {

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation
   * @return
   */
  def isDirectory(fileLocation: FileLocation): Boolean

  def exists(fileLocation: FileLocation): Boolean

  def createTemporaryFileLocation()(implicit directories: Directories): TemporaryFileLocation

  def lastModified(fileLocation: FileLocation): Long

}
