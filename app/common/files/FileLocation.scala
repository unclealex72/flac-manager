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
import java.time.Instant

import cats.data.ValidatedNel
import checkin.Mp3Encoder
import common.configuration.{Directories, User}
import common.files.Extension.{FLAC, MP3}
import common.music.{Tags, TagsService}
import logging.ApplicationLogging

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

  /**
   * Return whether this file location should be is read-only or not.
   * @return True if this file location should be read only, false otherwise.
   */
  def readOnly: Boolean

  /**
    * Return whether this file is a directory.
    * @param fileLocationExtensions The [[FileLocationExtensions]] used to add [[Path]]-like functionality to file locations.
    * @return True if this file location is a directory, false otherwise.
    */
  def isDirectory(implicit fileLocationExtensions: FileLocationExtensions): Boolean = fileLocationExtensions.isDirectory(this)

  /**
    * Return whether this file exists.
    * @param fileLocationExtensions The [[FileLocationExtensions]] used to add [[Path]]-like functionality to file locations.
    * @return True if this file location is exists, false otherwise.
    */
  def exists(implicit fileLocationExtensions: FileLocationExtensions): Boolean = fileLocationExtensions.exists(this)

  /**
    * Return the time this file was last modified.
    * @param fileLocationExtensions The [[FileLocationExtensions]] used to add [[Path]]-like functionality to file locations.
    * @return The instant when this file was last modified.
    */
  def lastModified(implicit fileLocationExtensions: FileLocationExtensions): Instant = fileLocationExtensions.lastModified(this)

  /**
    * Read and validate audio tags from this file.
    * @param tagsService The [[TagsService]] used to read and write tags.
    * @return The audio tags for this file or failure messages if the tags are incomplete.
    */
  def readTags(implicit tagsService: TagsService): ValidatedNel[String, Tags] = tagsService.read(toPath)

  /**
   * Resolve this file location to its absolute path.
   *
   * @return The absolute path of the file identified by this class.
   */
  protected[files] def toPath: Path = {
    basePath.resolve(relativePath)
  }

  /**
    * Write tags to this file.
    * @param tagsService The [[TagsService]] used to read and write tags.
    * @param tags The tags to write.
    */
  def writeTags(tags: Tags)(implicit tagsService: TagsService): Unit = tagsService.write(toPath, tags)
}

/**
 * A [[FileLocation]] in the temporary directory structure.
 */
trait TemporaryFileLocation extends FileLocation

/**
 * A [[FileLocation]] in the staging repository.
 */
trait StagedFlacFileLocation extends FileLocation {

  /**
    * Encode a staged flac file in to an MP3 file.
    * @param targetFileLocation The file to write the MP3 file to.
    * @param mp3Encoder The [[Mp3Encoder]] that will be used to encode this file.
    */
  def encodeTo(targetFileLocation: FileLocation)(implicit mp3Encoder: Mp3Encoder): Unit = {
    mp3Encoder.encode(this.toPath, targetFileLocation.toPath)
  }

  /**
    * Resolve where this file would be in the flac repository.
    * @param tags The tags for this file.
    * @return The flac file location where this staged file would be moved to.
    */
  def toFlacFileLocation(tags: Tags): FlacFileLocation

  /**
    * Determine whether this file is a valid flac file.
    * @param flacFileChecker The [[FlacFileChecker]] used to check whether this file is a flac file or not.
    * @return True if this file is a flac file, false otherwise.
    */
  def isFlacFile(implicit flacFileChecker: FlacFileChecker): Boolean = flacFileChecker.isFlacFile(toPath)

}

/**
 * A [[FileLocation]] in the FLAC repository.
 */
trait FlacFileLocation extends FileLocation {

  /**
    * The location where this file would be checked out to.
    * @return The location where this file would be checked out to.
    */
  def toStagedFlacFileLocation: StagedFlacFileLocation

  /**
    * The location where this file would be encoded to.
    * @return The location where this file would be encoded to.
    */
  def toEncodedFileLocation: EncodedFileLocation
}

/**
 * A [[FileLocation]] in the encoded repository.
 */
trait EncodedFileLocation extends FileLocation {

  /**
    * The location where this file can be found in a user's device repository.
    * @param user The user who's repository to use as a base.
    * @return
    */
  def toDeviceFileLocation(user: User): DeviceFileLocation

}

/**
 * A [[FileLocation]] in the devices repository.
 */
trait DeviceFileLocation extends FileLocation {

  /**
    * The user who owns the device.
    */
  val user: String

  /**
    * The flac file that would have been used to encode this file.
    * @return
    */
  def toFlacFileLocation: FlacFileLocation

  /**
    * The [[File]] at this file location.
    * @return The [[File]] at this file location.
    */
  def toFile(implicit fileLocationExtensions: FileLocationExtensions): File = path.toFile

  /**
    * Convert this location to a [[Path]].
    * @return The path for this location.
    */
  def path: Path = toPath

  def ifExists(implicit fileLocationExtensions: FileLocationExtensions): Option[DeviceFileLocation] = {
    if (exists && !isDirectory) Some(this) else None
  }

  /**
    * Get the first file if this location is a non empty directory. This is useful for finding album art.
    * @param fileLocationExtensions The typeclass used to give [[Path]]-like functionality to file locations.
    * @return The first file in this directory or none.
    */
  def firstInDirectory(implicit fileLocationExtensions: FileLocationExtensions): Option[DeviceFileLocation]

}

/**
  * Implicits for file locations.
  */
object FileLocationImplicits {

  /**
    * Order file locations by their path.
    * @tparam FL The file location type.
    * @return An ordering that orders file locations by their path.
    */
  implicit def fileLocationOrdering[FL <: FileLocation]: Ordering[FL] = Ordering.by(_.toPath)

  /**
   * An implicit class to add a resolve argument that then means that the FileLocation trait does not
   * need to be parameterised.
   * @param fileLocation The file location to enrich.
   * @tparam FL The type of the file location.
   */
  implicit class Resolver[FL <: FileLocation](val fileLocation: FL)(implicit val directories: Directories) {

    /**
      * Resolve a path using this file location as a base.
      * @param path The relative path to search for.
      * @return A new file location that is the original file location with the extra path added.
      */
    def resolve(path: Path): FL = {
      val newRelativePath: Path = fileLocation.relativePath.resolve(path)
      (fileLocation match {
        case _: FlacFileLocation => FlacFileLocationImpl(newRelativePath, directories)
        case _: StagedFlacFileLocation => StagedFlacFileLocationImpl(newRelativePath, directories)
        case _: EncodedFileLocation => EncodedFileLocationImpl(newRelativePath, directories)
        case _ => DeviceFileLocationImpl(fileLocation.asInstanceOf[DeviceFileLocationImpl].user, newRelativePath, directories)
      }).asInstanceOf[FL]
    }
  }
}

/**
  * The base class for all file locations.
  * @param prefixes A list of directories to prefix this location when printing as a string.
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param readOnly True if this repository is read only, false otherwise.
  * @param directoryFactory Get the root directory from a [[Directories]] class.
  * @param directories The location of all repositories.
  */
abstract class AbstractFileLocation(
                                     val prefixes: Seq[String],
                                     val relativePath: Path,
                                     val readOnly: Boolean,
                                     val directoryFactory: Directories => Path, val directories: Directories) {

  /**
    * The base path of the repository for this file location.
    */
  val basePath: Path = directoryFactory(directories)

  private val asString: String = (prefixes :+ relativePath.toString).mkString("/")
  override def toString: String =  asString


}

import common.files.PathImplicits._

/**
  * The default implementation of [[TemporaryFileLocationImpl]]
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param directories The location of all repositories.
  */
case class TemporaryFileLocationImpl(override val relativePath: Path, override val directories: Directories)
  extends AbstractFileLocation(Seq("tmp"), relativePath, false, _.temporaryPath, directories) with TemporaryFileLocation

/**
  * Used to create new [[TemporaryFileLocation]]s.
  */
object TemporaryFileLocation {

  /**
    * Get the temporary file location at the given relative paht.
    * @param relativePath The relative path to add the the temporary repository's base path.
    * @param directories The locations of the repositories.
    * @return A temporary file location.
    */
  def apply(relativePath: Path)(implicit directories: Directories): TemporaryFileLocation =
    TemporaryFileLocationImpl(relativePath, directories)

  /**
    * Create a new temporary file with an extension.
    * @param extension The extension to give the temporary file.
    * @param fileLocationExtensions The [[FileLocationExtensions]] typeclass used to add [[Path]]-like functionality to file locations.
    * @param directories The locations of the repositories.
    * @return A newly created temporary file with the given extension.
    */
  def create(extension: Extension)(implicit fileLocationExtensions: FileLocationExtensions, directories: Directories): TemporaryFileLocation =
    fileLocationExtensions.createTemporaryFileLocation(extension)

}

/**
  * The base class for flac file locations.
  * @param prefixes A list of directories to prefix this location when printing as a string.
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param readOnly True if this repository is read only, false otherwise.
  * @param directoryFactory Get the root directory from a [[Directories]] class.
  * @param directories The location of all repositories.
  */
sealed abstract class AbstractFlacFileLocation(
                                                override val prefixes: Seq[String],
                                                override val relativePath: Path,
                                                override val readOnly: Boolean,
                                                override val directoryFactory: Directories => Path,
                                                override implicit val directories: Directories)
  extends AbstractFileLocation(prefixes, relativePath, readOnly, directoryFactory, directories) {

  /**
    * @inheritdoc
    */
  def toStagedFlacFileLocation: StagedFlacFileLocation = StagedFlacFileLocation(relativePath)

  /**
    * @inheritdoc
    */
  def toEncodedFileLocation: EncodedFileLocation = EncodedFileLocation(relativePath withExtension MP3)

  /**
    * @inheritdoc
    */
  def toOwnedEncodedFileLocation(user: User): DeviceFileLocation = DeviceFileLocation(user, relativePath withExtension MP3)

}

/**
 * A helper object for pattern matching on FileLocations.
 */
private object Unapply extends ApplicationLogging {

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
  * The default implementation of [[StagedFlacFileLocation]].
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param directories The location of all repositories.
  */
case class StagedFlacFileLocationImpl(
                                       override val relativePath: Path,
                                       override val directories: Directories) extends AbstractFlacFileLocation(
  Seq("staging"), relativePath, false, _.stagingPath, directories) with StagedFlacFileLocation {

  /**
    * @inheritdoc
    */
  override def toFlacFileLocation(tags: Tags): FlacFileLocation = {
    FlacFileLocation(tags.asPath(FLAC))(directories)
  }
}

/**
  * Used to create [[StagedFlacFileLocation]]s
  */
object StagedFlacFileLocation {

  /**
    * Create a new staged flac file location.
    * @param path The first path segment of a relative path.
    * @param paths The next segments of a relative path.
    * @param directories The locations of the repositories.
    * @return A new staged flac file location pointing at a relative path.
    */
  def apply(path: String, paths: String*)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocation(Paths.get(path, paths: _*))

  /**
    * Create a new staged flac file location.
    * @param relativePath The path relative to the staging repository.
    * @param directories The locations of the repositories.
    * @return A new staged flac file location pointing at a relative path.
    */
  def apply(relativePath: Path)(implicit directories: Directories): StagedFlacFileLocation =
    StagedFlacFileLocationImpl(relativePath, directories)

  def unapply(absolutePath: Path)(implicit directories: Directories): Option[StagedFlacFileLocation] =
    Unapply(directories.stagingPath, absolutePath, p => StagedFlacFileLocation(p))
}

/**
  * The default implementation of [[FlacFileLocation]]
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param directories The location of all repositories.
  */
case class FlacFileLocationImpl(
                                 override val relativePath: Path, override val directories: Directories) extends AbstractFlacFileLocation(
  Seq("flac"), relativePath, true, _.flacPath, directories) with FlacFileLocation

/**
  * Used to create [[FlacFileLocation]]s.
  */
object FlacFileLocation {

  /**
    * Create a new flac file location.
    * @param path The first path segment of a relative path.
    * @param paths The next segments of a relative path.
    * @param directories The locations of the repositories.
    * @return A new flac file location pointing at a relative path.
    */
  def apply(path: String, paths: String*)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocation(Paths.get(path, paths: _*))

  /**
    * Create a new flac file location.
    * @param relativePath The path relative to the flac repository.
    * @param directories The locations of the repositories.
    * @return A new flac file location pointing at a relative path.
    */
  def apply(relativePath: Path)(implicit directories: Directories): FlacFileLocation =
    FlacFileLocationImpl(relativePath, directories)

  def unapply(absolutePath: Path)(implicit directories: Directories): Option[FlacFileLocation] =
    Unapply(directories.flacPath, absolutePath, p => FlacFileLocation(p))
}

/**
  * The default implementation of [[EncodedFileLocation]].
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param directories The location of all repositories.
  */
case class EncodedFileLocationImpl(
                                    override val relativePath: Path, override val directories: Directories) extends AbstractFileLocation(
  Seq("encoded"), relativePath, true, _.encodedPath, directories) with EncodedFileLocation {

  /**
    * @inheritdoc
    */
  override def toDeviceFileLocation(user: User): DeviceFileLocation = {
    DeviceFileLocation(user, relativePath)(directories)
  }
}

/**
  * Create [[EncodedFileLocation]]s.
  */
object EncodedFileLocation {

  /**
    * Create a new encoded file location.
    * @param path The first path segment of a relative path.
    * @param paths The next segments of a relative path.
    * @param directories The locations of the repositories.
    * @return A new encoded file location pointing at a relative path.
    */
  def apply(path: String, paths: String*)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocation(Paths.get(path, paths: _*))

  /**
    * Create a new encoded file location.
    * @param relativePath The path relative to the encoded repository.
    * @param directories The locations of the repositories.
    * @return A new encoded file location pointing at a relative path.
    */
  def apply(relativePath: Path)(implicit directories: Directories): EncodedFileLocation = EncodedFileLocationImpl(relativePath, directories)

}

/**
  * The default implementation of [[DeviceFileLocation]].
  * @param user The user who own's the device.
  * @param relativePath The relative path of the file location, relative to its repository's root.
  * @param directories The location of all repositories.
  */
case class DeviceFileLocationImpl(
                                   override val user: String, override val relativePath: Path, override val directories: Directories)
  extends AbstractFileLocation(
    Seq("devices", user), relativePath, true, _.devicesPath.resolve(user), directories) with DeviceFileLocation {

  /**
    * @inheritdoc
    */
  override def toFlacFileLocation: FlacFileLocation = FlacFileLocation(relativePath withExtension FLAC)(directories)

  /**
    * @inheritdoc
    */
  def firstInDirectory(implicit fileLocationExtensions: FileLocationExtensions): Option[DeviceFileLocation] = {
    fileLocationExtensions.firstFileIn(this, MP3, path => DeviceFileLocationImpl(user, path, directories))
  }
}

/**
  * Create [[DeviceFileLocation]]s.
  */
object DeviceFileLocation {

  /**
    * Point to the root of a user's device repository.
    * @param user The user who owns the device.
    * @param directories The locations of the repositories.
    * @return The root of the user's device repository.
    */
  def apply(user: User)(implicit directories: Directories): DeviceFileLocation = apply(user.name)

  /**
    * Point to the root of a user's device repository.
    * @param user The name of the user who owns the device.
    * @param directories The locations of the repositories.
    * @return The root of the user's device repository.
    */
  def apply(user: String)(implicit directories: Directories): DeviceFileLocation = apply(user, "")

  /**
    * Create a new flac file location.
    * @param user The user who owns the device.
    * @param relativePath The path relative to the user's device repository.
    * @param directories The locations of the repositories.
    * @return A new device file location pointing at a relative path.
    */
  def apply(user: User, relativePath: Path)(implicit directories: Directories): DeviceFileLocation = apply(user.name, relativePath)

  /**
    * Create a new device file location.
    * @param user The user who owns the device.
    * @param path The first path segment of a relative path.
    * @param paths The next segments of a relative path.
    * @param directories The locations of the repositories.
    * @return A new device file location pointing at a relative path.
    */
  def apply(user: User, path: String, paths: String*)(implicit directories: Directories): DeviceFileLocation =
    apply(user.name, path, paths: _*)

  /**
    * Create a new device file location.
    * @param user The name of user who owns the device.
    * @param path The first path segment of a relative path.
    * @param paths The next segments of a relative path.
    * @param directories The locations of the repositories.
    * @return A new device file location pointing at a relative path.
    */
  def apply(user: String, path: String, paths: String*)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocation(user, Paths.get(path, paths: _*))

  /**
    * Create a new flac file location.
    * @param user The name of the user who owns the device.
    * @param relativePath The path relative to the user's device repository.
    * @param directories The locations of the repositories.
    * @return A new device file location pointing at a relative path.
    */
  def apply(user: String, relativePath: Path)(implicit directories: Directories): DeviceFileLocation =
    DeviceFileLocationImpl(user, relativePath, directories)
}

/**
  * Contains ordering for [[FileLocation]]s.
  */
object FileLocation {

  implicit def ordering[FL <: FileLocation]: Ordering[FL] = Ordering.by(_.toPath)
}