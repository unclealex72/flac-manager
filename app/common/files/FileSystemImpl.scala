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


import java.io.IOException
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject

import common.message.Messages._
import common.message._

import scala.util.Try
import scala.compat.java8.StreamConverters._

/**
 * The default implementation of[[FileSystem]].
 */
class FileSystemImpl @Inject() extends FileSystem with Messaging {

  /**
    * @inheritdoc
    */
  override def move(sourceFile: File, targetFile: File)
                   (implicit messageService: MessageService): Unit = {
    log(MOVE(sourceFile, targetFile))
    val sourcePath = sourceFile.absolutePath
    val targetPath = targetFile.absolutePath
    Files.createDirectories(targetPath.getParent)
    tryAtomicMove(sourcePath, targetPath)
    val currentDirectory = sourcePath.getParent
    remove(sourceFile.basePath, currentDirectory)
  }

  /**
    * @inheritdoc
    */
  override def copy(sourceFile: File, targetFile: File)
                   (implicit messageService: MessageService): Unit = {
    val sourcePath = sourceFile.absolutePath
    val targetPath = targetFile.absolutePath
    val parentTargetPath = targetPath.getParent
    Files.createDirectories(parentTargetPath)
    val tempPath = Files.createTempFile(parentTargetPath, "device-file-", ".tmp")
    Files.copy(sourcePath, tempPath, StandardCopyOption.REPLACE_EXISTING)
    tryAtomicMove(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    val currentDirectory = sourcePath.getParent
    remove(sourceFile.basePath, currentDirectory)
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
  override def remove(file: File)(implicit messageService: MessageService): Unit = {
    log(DELETE(file))
    remove(file.basePath, file.absolutePath)
  }

  /**
    * Remove a path and, if it's directory is now empty, remove that and recurse up.
    * @param basePath The base path at which to no longer try to remove directories.
    * @param path The path to remove.
    * @return A [[Try]] containing an exception if one occurred.
    */
  def remove(basePath: Path, path: Path): Try[Unit] = Try {
    if (basePath.equals(path)) {
      // Do nothing
    }
    else if (Files.isDirectory(path)) {
      val dir = Files.list(path)
      val directoryIsEmpty = dir.toScala[Seq].forall(Files.isHidden)
      if (directoryIsEmpty) {
        recursivelyDelete(path)
        remove(basePath, path.getParent)
      }
      dir.close()
    }
    else {
      Files.deleteIfExists(path)
      remove(basePath, path.getParent)
    }
  }

  def recursivelyDelete(path: Path): Unit = {
    def deleteAndContinue(path: Path): FileVisitResult = {
      Files.delete(path)
      FileVisitResult.CONTINUE
    }
    Files.walkFileTree(path, new SimpleFileVisitor[Path] {
      override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = deleteAndContinue(dir)
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = deleteAndContinue(file)
    })
  }
  /**
    * @inheritdoc
    *
    */
  override def link(file: File, link: File)(implicit messageService: MessageService): Unit = {
    log(LINK(file, link))
    val target = file.absolutePath
    val lnk = link.absolutePath
    val parent = lnk.getParent
    val relativeTarget = parent.relativize(target)
    Files.createDirectories(parent)
    Files.deleteIfExists(lnk)
    Files.createSymbolicLink(lnk, relativeTarget)
  }
}
