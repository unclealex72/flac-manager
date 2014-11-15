/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package files

import java.nio.file.{Files, Path, Paths}

import common.files.{FileUtilsImpl, TestFileLocation}
import common.message.MessageTypes._
import common.message.TestMessageService
import org.specs2.mock.Mockito
import org.specs2.mutable._
import tempfs.TempFileSystem

/**
 * @author alex
 *
 */
class FileUtilsImplSpec extends Specification with PathMatchers with Mockito {

  val fileUtils = new FileUtilsImpl

  trait fs extends TempFileSystem {
    lazy val source = rootDirectory.resolve("source")
    lazy val target = rootDirectory.resolve("target")
    implicit val messageService: TestMessageService = mock[TestMessageService]

    def before(rootDirectory: Path): Unit = {}
  }

  "Moving a file with siblings" should {
    "move only the file and not its siblings" in new fs {
      Files.createDirectories(target)
      val fileToMove = TestFileLocation(source, "dir", "moveme.txt")
      val fileToKeep = TestFileLocation(source, "dir", "keepme.txt")
      Seq(fileToMove, fileToKeep).foreach { fl =>
        Files.createDirectories(fl.toPath.getParent());
        Files.createFile(fl.toPath);
      }
      val targetLocation = TestFileLocation(target, "otherdir", "movedme.txt")
      fileUtils.move(fileToMove, targetLocation)
      target.resolve(Paths.get("otherdir", "movedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "movedme.txt")) must not(beADirectory)
      fileToKeep.toPath must exist
      fileToMove.toPath must not(exist)
      there was one(messageService).printMessage(MOVE(fileToMove, targetLocation))
    }
  }

  "Linking to a file" should {
    "create a relative link that points to the original file" in new fs {
      val targetLocation = TestFileLocation(rootDirectory, "here.txt")
      Files.createFile(targetLocation.toPath);
      val linkLocation = TestFileLocation(rootDirectory, "link.d", "link.txt")
      fileUtils.link(targetLocation, linkLocation)
      linkLocation.toPath must beASymbolicLink
      val symlink = Files.readSymbolicLink(linkLocation.toPath)
      symlink must not(beAbsolute)
      linkLocation.toPath.getParent.resolve(symlink).toAbsolutePath must beTheSameFileAs(targetLocation.toPath.toAbsolutePath())
      there was one(messageService).printMessage(LINK(targetLocation, linkLocation))
    }
  }

  "Moving a file without siblings" should {
    "move the file and remove all empty directories" in new fs {
      Files.createDirectories(target)
      val fileToMove = TestFileLocation(source, "dir", "moveme.txt")
      Files.createDirectories(fileToMove.toPath.getParent())
      Files.createFile(fileToMove.toPath)
      val targetLocation = TestFileLocation(target, "otherdir", "movedme.txt")
      fileUtils.move(fileToMove, targetLocation)
      target.resolve(Paths.get("otherdir", "movedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "movedme.txt")) must not(beADirectory)
      fileToMove.toPath.getParent must not(exist)
      source must exist
      there was one(messageService).printMessage(MOVE(fileToMove, targetLocation))
    }
  }

  "Copying a file" should {
    "also create any required missing directories" in new fs {
      Files.createDirectories(target)
      val fileToCopy = TestFileLocation(source, "dir", "copyme.txt")
      Files.createDirectories(fileToCopy.toPath.getParent)
      Files.createFile(fileToCopy.toPath)
      fileUtils.copy(fileToCopy, TestFileLocation(target, "otherdir", "copiedme.txt"))
      target.resolve(Paths.get("otherdir", "copiedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "copiedme.txt")) must not(beADirectory)
      fileToCopy.toPath must exist
    }
  }

  "Checking whether a file is a directory or not" should {
    "return true for a directory" in new fs {
      Files.createDirectories(source.resolve("dir"))
      fileUtils.isDirectory(TestFileLocation(source, "dir")) must beTrue
    }
    "return false for a file" in new fs {
      Files.createDirectories(source)
      Files.createFile(source.resolve("file"))
      fileUtils.isDirectory(TestFileLocation(source, "file")) must beFalse
    }
    "return false for a file that does not exist" in new fs {
      Files.createDirectories(source)
      fileUtils.isDirectory(TestFileLocation(source, "nodir")) must beFalse
    }
  }
}
