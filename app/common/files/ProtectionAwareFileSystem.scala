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

import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Files, Path}
import java.util

import com.typesafe.scalalogging.StrictLogging
import javax.inject.{Inject, Named}

import scala.collection.JavaConverters._

/**
  * An implementation of [[FileSystem]] that decorates another [[FileSystem]] and is aware of whether [[FileSystem]]s
  * should be left in a read only or writable state.
  * @param delegate The file system to delegate to.
  *
  */
class ProtectionAwareFileSystem @Inject() (@Named("rawFileSystem") val delegate: FileSystem)
  extends DecoratingFileSystem with StrictLogging {

  /**
    * Make all files that need to be writeable.
    * @param files The file s to send to the original invocation.
    */
  override def before(files: Seq[File]): Unit = alterWritable(_ => true, files)

  /**
    * Make all files that need to be unrwiteable.
    * @param files The file s to send to the original invocation.
    */
  def after(files: Seq[File]): Unit = alterWritable(!_.readOnly, files)

  /**
    * Alter all files and their parents (up to the repository base) to be either writeable or unwriteable.
    * @param writable True if files should be made writeable, false otherwise.
    * @param files The file s to send to the original invocation.
    */
  def alterWritable(writable: File => Boolean, files: Seq[File]): Unit = {
    files.foreach { file =>
      val terminatingPath: Path = file.rootPath.getParent
      Stream.iterate(file.absolutePath)(_.getParent).takeWhile(path => path != null && path != terminatingPath).foreach { currentPath =>
        if (Files.exists(currentPath)) {
          val posixFilePermissions: util.Set[PosixFilePermission] = Files.getPosixFilePermissions(currentPath)
          val isWritable = writable(file)
          if (isWritable) {
            logger.debug("Setting " + currentPath + " to read and write.")
            posixFilePermissions.add(PosixFilePermission.OWNER_WRITE)
          }
          else {
            logger.debug("Setting " + currentPath + " to read only.")
            posixFilePermissions.removeAll(Seq(
              PosixFilePermission.OWNER_WRITE,
              PosixFilePermission.GROUP_WRITE,
              PosixFilePermission.OTHERS_WRITE).asJavaCollection)
          }
          Files.setPosixFilePermissions(currentPath, posixFilePermissions)
        }
      }
    }
  }
}
