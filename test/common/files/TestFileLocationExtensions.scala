package common.files

import common.configuration.Directories

/**
 * Created by alex on 16/11/14.
 */
trait TestFileLocationExtensions extends FileLocationExtensions {

  /**
   * Return true if the file location points to a directory, false otherwise.
   * @param fileLocation
   * @return
   */
  def isDirectory(fileLocation: FileLocation): Boolean = false

  def exists(fileLocation: FileLocation): Boolean = false

  def createTemporaryFileLocation(extension: Extension)(implicit directories: Directories): TemporaryFileLocation = null

  def lastModified(fileLocation: FileLocation): Long = 0

}
