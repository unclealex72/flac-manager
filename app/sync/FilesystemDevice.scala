/*
 * Copyright 2015 Alex Jones
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

package sync

import java.io.IOException
import java.nio.file.{Files, Path}

import common.configuration.Directories
import common.files.{DeviceFileLocation, FileSystem, RemovableFileLocation}
import common.message.MessageService

import scala.collection.JavaConversions._

/**
 * Created by alex on 24/07/15.
 */
class FilesystemDeviceFactory(val fileSystem: FileSystem)(implicit val directories: Directories) {

  def create(owner: String, mountPoint: Path, subdirectory: String): Device = new FilesystemDevice(owner, mountPoint, subdirectory, fileSystem)
}

class FilesystemDevice(override val owner: String, override val mountPoint: Path, val subdirectory: String, val fileSystem: FileSystem)(implicit val directories: Directories) extends Device {

  override val name: String = "USB"
  val rootDirectory = mountPoint.resolve(subdirectory)

  override def isConnected(implicit messageService: MessageService) = Files.isDirectory(rootDirectory)

  /**
   * Add a new file to the device.
   *
   * The location of the file to add.
   * @throws IOException
   * Signals that an I/O exception has occurred.
   */
  override def add(deviceFileLocation: DeviceFileLocation)(implicit messageService: MessageService): Unit = {
    fileSystem.copy(deviceFileLocation, deviceFileLocation.toRemovableFileLocation(rootDirectory))
  }

  /**
   * List all the files currently on the device.
   *
   * @return A set of { @link DeviceFile}s that represent the files currently on
   *         the device.
   * @throws IOException
   * Signals that an I/O exception has occurred.
   */
  override def listDeviceFiles(implicit messageService: MessageService): Set[DeviceFile] = {
    def pathLister(directory: Path): Set[Path] = {
      Files.newDirectoryStream(directory).foldLeft(Set.empty[Path]) { (paths, path) =>
        if (Files.isDirectory(path)) {
          paths ++ pathLister(path)
        }
        else {
          paths + path
        }
      }
    }
    val paths = pathLister(rootDirectory)
    paths.map { path =>
      val relativePath = rootDirectory.relativize(path).toString
      val lastModified = Files.getLastModifiedTime(path).toMillis
      DeviceFile(relativePath, relativePath, lastModified)
    }
  }

  /**
   * Subclasses need to extend this method to include any logic that needs to be
   * executed after the device has been unmounted.
   *
   * @throws IOException
   */
  override def afterUnmount(implicit messageService: MessageService): Unit = {
    // Do nothing
  }

  /**
   * Remove a file from the device.
   *
   * @param deviceFile
   * The device file to remove.
   * @throws IOException
   * Signals that an I/O exception has occurred.
   */
  override def remove(deviceFile: DeviceFile)(implicit messageService: MessageService): Unit = {
    val fileLocation = RemovableFileLocation(rootDirectory, deviceFile.relativePath)
    fileSystem.remove(fileLocation)
  }

  /**
   * Subclasses need to override this method to contain any device based logic
   * that needs to be executed after the device is mounted but before any file
   * searching or copying takes place.
   *
   * The path where the device has been mounted.
   * @throws IOException
   */
  override def afterMount(implicit messageService: MessageService): Unit = {
    // Do nothing
  }

  /**
   * Subclasses need to override this method to include any device logic that
   * needs to be executed after the device has been synchronised but before the
   * device is unmounted.
   *
   * @throws IOException
   */
  override def beforeUnmount(implicit messageService: MessageService): Unit = {
    // Do nothing
  }
}
