/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.files

import java.io.File
import java.nio.file.{Path, Paths}

import checkin.Mp3Encoder
import com.typesafe.scalalogging.StrictLogging
import common.configuration.{Directories, User}
import common.music.{Tags, TagsService}
import sync.Device

import scalaz.ValidationNel

/**
 * A class that encapsulates a location of a file within a repository of music
 * files. This is a reusable component because the general actions on files
 * require doing something to a file and then making sure that some sort of
 * change is then rippled up to the base of the repository.
 *
 * @author alex
 *
 */
trait FileLocation {
  /**
   * The location of the file relative to the base path.
   */
  val relativePath: Path
  /**
   * The base path of the repository.
   */
  protected[files] val basePath: Path

  def toMessage: String = toPath.toString

  /**
   * True if this file location should be read only, false otherwise.
   * @return
   */
  def readOnly: Boolean

  def isDirectory(implicit fileLocationExtensions: FileLocationExtensions): Boolean = fileLocationExtensions.isDirectory(this)

  def exists(implicit fileLocationExtensions: FileLocationExtensions): Boolean = fileLocationExtensions.exists(this)

  def lastModified(implicit fileLocationExtensions: FileLocationExtensions): Long = fileLocationExtensions.lastModified(this)

  def readTags(implicit tagsService: TagsService): ValidationNel[String, Tags] = tagsService.read(toPath)

  /**
   * Resolve this file location to its absolute path.
   *
   * @return The absolute path of the file identified by this class.
   */
  protected[files] def toPath: Path = {
    basePath.resolve(relativePath);
  }

  def writeTags(tags: Tags)(implicit tagsService: TagsService): Unit = tagsService.write(toPath, tags)
}

/**
 * A file location in the temporary directory structure.
 */
trait TemporaryFileLocation extends FileLocation

/**
 * A `FileLocation` in the staging repository.
 */
trait StagedFlacFileLocation extends FileLocation {

  def encodeTo(targetFileLocation: FileLocation)(implicit mp3Encoder: Mp3Encoder): Unit = {
    mp3Encoder.encode(this.toPath, targetFileLocation.toPath)
  }

  def toFlacFileLocation(tags: Tags): FlacFileLocation

  def isFlacFile(implicit flacFileChecker: FlacFileChecker): Boolean = flacFileChecker.isFlacFile(toPath)

}

/**
 * A `FileLocation` in the FLAC repository.
 */
trait FlacFileLocation extends FileLocation {

  def toStagedFlacFileLocation: StagedFlacFileLocation

  def toEncodedFileLocation: EncodedFileLocation
}

trait ToDeviceFileLocation {
  def toDeviceFileLocation(user: User): DeviceFileLocation
}

trait ToDeviceFileLocationImpl extends ToDeviceFileLocation {
  this: {
    val directories: Directories
    val relativePath: Path
  } =>

  override def toDeviceFileLocation(user: User): DeviceFileLocation = DeviceFileLocation(user, relativePath)(directories)

}

/**
 * A `FileLocation` in the encoded repository.
 */
trait EncodedFileLocation extends FileLocation with ToDeviceFileLocation

/**
 * A `FileLocation` in the devices repository.
 */
trait DeviceFileLocation extends FileLocation {

  val user: String

  def toFlacFileLocation: FlacFileLocation

  def toRemovableFileLocation(rootDirectory: Path): RemovableFileLocation

  def toFile(implicit fileLocationExtensions: FileLocationExtensions): File = path.toFile

  def path: Path = toPath

  def ifExists(implicit fileLocationExtensions: FileLocationExtensions): Option[DeviceFileLocation] = {
    if (exists && !isDirectory) Some(this) else None
  }
}

trait RemovableFileLocation extends FileLocation with ToDeviceFileLocation {

  def toFile(implicit fileLocationExtensions: FileLocationExtensions): File = path.toFile

  def path: Path = toPath

}


object FileLocationImplicits {

  implicit def fileLocationOrdering[FL <: FileLocation]: Ordering[FL] = Ordering.by(_.toPath)

  /**
   * An implicit class to add a resolve argument that then means that the FileLocation trait does not
   * need to be parameterised.
   * @param fileLocation
   * @tparam FL
   */
  implicit class Resolver[FL <: FileLocation](val fileLocation: FL)(implicit val directories: Directories) {

    def extendTo(path: Path): FL = {
      val newRelativePath: Path = fileLocation.relativePath.resolve(path)
      (if (fileLocation.isInstanceOf[FlacFileLocation]) FlacFileLocationImpl(newRelativePath, directories)
      else if (fileLocation.isInstanceOf[StagedFlacFileLocation]) StagedFlacFileLocationImpl(newRelativePath, directories)
      else if (fileLocation.isInstanceOf[EncodedFileLocation]) EncodedFileLocationImpl(newRelativePath, directories)
      else DeviceFileLocationImpl(fileLocation.asInstanceOf[DeviceFileLocationImpl].user, newRelativePath, directories)).asInstanceOf[FL]
    }
  }
}

abstract class AbstractFileLocation(
                                     val name: String,
                                     val relativePath: Path,
                                     val readOnly: Boolean,
                                     val directoryFactory: Directories => Path,
                                     val directories: Directories) {

  val basePath = directoryFactory(directories)

  override def toString: String = s"$name($relativePath)"


}

import common.files.PathImplicits._

case class TemporaryFileLocationImpl(override val relativePath: Path, override val directories: Directories)
  extends AbstractFileLocation("TemporaryFileLocation", relativePath, false, _.temporaryPath, directories) with TemporaryFileLocation

object TemporaryFileLocation {

  def apply(relativePath: Path)(implicit directories: Directories): TemporaryFileLocation =
    TemporaryFileLocationImpl(relativePath, directories)

  def create(extension: Extension)(implicit fileLocationExtensions: FileLocationExtensions, directories: Directories): TemporaryFileLocation =
    fileLocationExtensions.createTemporaryFileLocation(extension)

}

sealed abstract class AbstractFlacFileLocation(
                                                override val name: String,
                                                override val relativePath: Path,
                                                override val readOnly: Boolean,
                                                override val directoryFactory: Directories => Path,
                                                override implicit val directories: Directories) extends AbstractFileLocation(name, relativePath, readOnly, directoryFactory, directories) {

  def toStagedFlacFileLocation: StagedFlacFileLocation = StagedFlacFileLocation(relativePath)

  def toEncodedFileLocation: EncodedFileLocation = EncodedFileLocation(relativePath withExtension MP3)

  def toOwnedEncodedFileLocation(device: Device): DeviceFileLocation = DeviceFileLocation(device.owner, relativePath withExtension MP3)

}

/**
 * A helper object for pattern matching on FileLocations.
 */
private object Unapply extends StrictLogging {

  def apply[F <: FileLocation](basePath: Path, absolutePath: Path, factory: Path => F): Option[F] = {
    if (absolutePath.startsWith(basePath)) {
      Some(factory(basePath.relativize(absolutePath)))
    }
    else {
      logger.debug(s"Rejecting $absolutePath as it is not a subpath of $basePath")
      None
    }
  }
}

/**
 * Staged Flac files
 * @param relativePath
 * @param directories
 */
case class StagedFlacFileLocationImpl(
                                       override val relativePath: Path,
                                       override val directories: Directories) extends AbstractFlacFileLocation(
  "StagedFlacFileLocation", relativePath, false, _.stagingPath, directories) with StagedFlacFileLocation {

  override def toFlacFileLocation(tags: Tags): FlacFileLocation = {
    FlacFileLocation(tags.asPath(FLAC))(directories)
  }
}

object StagedFlacFileLocation {

  def apply(path: String, paths: String*)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocation(Paths.get(path, paths: _*))

  def apply(relativePath: Path)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocationImpl(relativePath, directories)

  def unapply(absolutePath: Path)(implicit directories: Directories): Option[StagedFlacFileLocation] =
    Unapply(directories.stagingPath, absolutePath, p => StagedFlacFileLocation(p))
}

/**
 * Flac files
 * @param relativePath
 * @param directories
 */
case class FlacFileLocationImpl(
                                 override val relativePath: Path, override val directories: Directories) extends AbstractFlacFileLocation(
  "FlacFileLocation", relativePath, true, _.flacPath, directories) with FlacFileLocation

object FlacFileLocation {

  def apply(path: String, paths: String*)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocation(Paths.get(path, paths: _*))

  def unapply(absolutePath: Path)(implicit directories: Directories): Option[FlacFileLocation] =
    Unapply(directories.flacPath, absolutePath, p => FlacFileLocation(p))

  def apply(relativePath: Path)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocationImpl(relativePath, directories)
}

/**
 * Encoded files
 * @param relativePath
 * @param directories
 */
case class EncodedFileLocationImpl(
                                    override val relativePath: Path, override val directories: Directories) extends AbstractFileLocation(
  "EncodedFlacFileLocation", relativePath, true, _.encodedPath, directories) with EncodedFileLocation with ToDeviceFileLocationImpl {
}

object EncodedFileLocation {

  def apply(path: String, paths: String*)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocation(Paths.get(path, paths: _*))

  def apply(relativePath: Path)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocationImpl(relativePath, directories)

}

/**
 * Owned encoded files
 * @param relativePath
 * @param directories
 */
case class DeviceFileLocationImpl(
                                   override val user: String, override val relativePath: Path, override val directories: Directories)
  extends AbstractFileLocation(
    "DeviceFileLocation", relativePath, true, _.devicesPath.resolve(user), directories) with DeviceFileLocation {

  override def toFlacFileLocation: FlacFileLocation = FlacFileLocation(relativePath withExtension FLAC)(directories)

  override def toRemovableFileLocation(rootDirectory: Path): RemovableFileLocation = RemovableFileLocation(rootDirectory, relativePath)(directories)

}

object DeviceFileLocation {

  def apply(user: User)(implicit directories: Directories): DeviceFileLocation = apply(user.name)

  def apply(user: String)(implicit directories: Directories): DeviceFileLocation = apply(user, "")

  def apply(user: User, relativePath: Path)(implicit directories: Directories): DeviceFileLocation = apply(user.name, relativePath)

  def apply(user: User, path: String, paths: String*)(implicit directories: Directories): DeviceFileLocation =
    apply(user.name, path, paths: _*)

  def apply(user: String, path: String, paths: String*)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocation(user, Paths.get(path, paths: _*))

  def apply(user: String, relativePath: Path)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocationImpl(user, relativePath, directories)
}

case class RemovableFileLocationImpl(val rootDirectory: Path, override val relativePath: Path, override val directories: Directories) extends AbstractFileLocation(
  "RemovableFileLocation", relativePath, true, _ => rootDirectory, directories) with RemovableFileLocation with ToDeviceFileLocationImpl

object RemovableFileLocation {

  def apply(rootDirectory: Path)(implicit directories: Directories): RemovableFileLocation = apply(rootDirectory, "")

  def apply(rootDirectory: Path, path: String, paths: String*)(implicit directories: Directories): RemovableFileLocation =
    RemovableFileLocation(rootDirectory, Paths.get(path, paths: _*))

  def apply(rootDirectory: Path, relativePath: Path)(implicit directories: Directories): RemovableFileLocation =
    RemovableFileLocationImpl(rootDirectory, relativePath, directories)
}
