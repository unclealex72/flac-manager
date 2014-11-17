package common.files

import java.nio.file.{Files, Path}

import common.configuration.Directories

/**
 * Created by alex on 16/11/14.
 */
class FileLocationExtensionsImpl extends FileLocationExtensions {

  implicit val fileLocationToPath: FileLocation => Path = _.toPath

  override def isDirectory(fileLocation: FileLocation) = {
    Files.exists(fileLocation) && !Files.isSymbolicLink(fileLocation) && Files.isDirectory(fileLocation)
  }

  def exists(fileLocation: FileLocation) = Files.exists(fileLocation)

  override def createTemporaryFileLocation()(implicit directories: Directories): TemporaryFileLocation = {
    val path = Files.createTempFile(directories.temporaryPath, "flac-manager-", ".tmp")
    TemporaryFileLocation(directories.temporaryPath.resolve(path))
  }

  override def lastModified(fileLocation: FileLocation): Long = {
    Files.getLastModifiedTime(fileLocation).toMillis
  }
}
