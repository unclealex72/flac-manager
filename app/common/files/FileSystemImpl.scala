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


import java.nio.file.{AtomicMoveNotSupportedException, Files, Path, StandardCopyOption}
import javax.inject.Inject

import common.message.Messages._
import common.message._

import scala.util.Try


/**
 * The default implementation of[[FileSystem]].
 */
class FileSystemImpl @Inject() extends FileSystem with Messaging {

  /**
    * @inheritdoc
    */
  override def move(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)
                   (implicit messageService: MessageService): Unit = {
    log(MOVE(sourceFileLocation, targetFileLocation))
    val sourcePath = sourceFileLocation.toPath
    val targetPath = targetFileLocation.toPath
    Files.createDirectories(targetPath.getParent)
    tryAtomicMove(sourcePath, targetPath)
    val currentDirectory = sourcePath.getParent
    remove(sourceFileLocation.basePath, currentDirectory)
  }

  /**
    * @inheritdoc
    */
  override def copy(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)
                   (implicit messageService: MessageService): Unit = {
    val sourcePath = sourceFileLocation.toPath
    val targetPath = targetFileLocation.toPath
    val parentTargetPath = targetPath.getParent
    Files.createDirectories(parentTargetPath)
    val tempPath = Files.createTempFile(parentTargetPath, "device-file-", ".tmp")
    Files.copy(sourcePath, tempPath, StandardCopyOption.REPLACE_EXISTING)
    tryAtomicMove(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    val currentDirectory = sourcePath.getParent
    remove(sourceFileLocation.basePath, currentDirectory)
  }

  /**
    * Try and move a file using an atomic move operation. If this fails, move it non-atomically.
    * @param sourcePath The source path.
    * @param targetPath The target path.
    * @param options The [[StandardCopyOption]] passed to the file system.
    */
  def tryAtomicMove(sourcePath: Path, targetPath: Path, options: StandardCopyOption*): Unit = {
    try {
      val optionsWithAtomicMove = options :+ StandardCopyOption.ATOMIC_MOVE
      Files.move(sourcePath, targetPath, optionsWithAtomicMove: _*)
    }
    catch {
      case _: AtomicMoveNotSupportedException =>
        Files.move(sourcePath, targetPath, options: _*)
    }
  }

  /**
    * @inheritdoc
    */
  override def remove(fileLocation: FileLocation)(implicit messageService: MessageService): Unit = {
    log(DELETE(fileLocation))
    remove(fileLocation.basePath, fileLocation.toPath)
  }

  /**
    * Remove a path and, if it's directory is now empty, remove that and recurse up.
    * @param basePath The base path at which to no longer try to remove directories.
    * @param path The path to remove.
    * @return A [[Try]] containing an exception if one occurred.
    */
  def remove(basePath: Path, path: Path): Try[Unit] = Try {
    if (Files.isSameFile(basePath, path)) {
      // Do nothing
    }
    else if (Files.isDirectory(path)) {
      val dir = Files.newDirectoryStream(path)
      val directoryIsEmpty = !dir.iterator().hasNext
      if (directoryIsEmpty) {
        Files.delete(path)
        remove(basePath, path.getParent)
      }
      dir.close()
    }
    else {
      Files.deleteIfExists(path)
      remove(basePath, path.getParent)
    }
  }

  /**
    * @inheritdoc
    *
    */
  override def link(fileLocation: FileLocation, linkLocation: FileLocation)(implicit messageService: MessageService): Unit = {
    log(LINK(fileLocation, linkLocation))
    val target = fileLocation.toPath
    val link = linkLocation.toPath
    val parent = link.getParent
    Files.createDirectories(parent)
    val relativeTarget = parent.relativize(target)
    Files.createSymbolicLink(link, relativeTarget)
  }
}
