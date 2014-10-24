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

import java.nio.file.{Paths, Files, Path}

import org.specs2.matcher.{Expectable, Matcher}
import org.specs2.mutable._

/**
 * @author alex
 *
 */
class FileUtilsImplSpec extends Specification with PathMatchers {

  val fileUtils = new FileUtilsImpl

  trait fs extends TempFileSystem {
    lazy val source = rootDirectory.resolve("source")
    lazy val target = rootDirectory.resolve("target")

    def before(rootDirectory: Path): Unit = {}
  }

  "Moving a file with siblings" should {
    "move only the file and not its siblings" in new fs {
      Files.createDirectories(target)
      val fileToMove = FileLocation(source, Paths.get("dir", "moveme.txt"), false)
      val fileToKeep = FileLocation(source, Paths.get("dir", "keepme.txt"), false)
      Seq(fileToMove, fileToKeep).foreach { fl =>
        Files.createDirectories(fl.resolve.getParent());
        Files.createFile(fl.resolve);
      }
      fileUtils.move(fileToMove, new FileLocation(target, Paths.get("otherdir", "movedme.txt"), false))
      target.resolve(Paths.get("otherdir", "movedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "movedme.txt")) must not(beADirectory)
      fileToKeep.resolve must exist
      fileToMove.resolve must not(exist)
    }
  }

  "Linking to a file" should {
    "create a relative link that points to the original file" in new fs {
      val targetLocation = FileLocation(rootDirectory, Paths.get("here.txt"), false)
      Files.createFile(targetLocation.resolve);
      val linkLocation = FileLocation(rootDirectory, Paths.get("link.d", "link.txt"), false)
      fileUtils.link(targetLocation, linkLocation)
      linkLocation.resolve must beASymbolicLink
      val symlink = Files.readSymbolicLink(linkLocation.resolve)
      symlink must not(beAbsolute)
      linkLocation.resolve.getParent.resolve(symlink).toAbsolutePath must beTheSameFileAs(targetLocation.resolve.toAbsolutePath())
    }
  }

  "Moving a file without siblings" should {
    "move the file and remove all empty directories" in new fs {
      Files.createDirectories(target)
      val fileToMove = FileLocation(source, Paths.get("dir", "moveme.txt"), false)
      Files.createDirectories(fileToMove.resolve.getParent())
      Files.createFile(fileToMove.resolve)
      fileUtils.move(fileToMove, new FileLocation(target, Paths.get("otherdir", "movedme.txt"), false))
      target.resolve(Paths.get("otherdir", "movedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "movedme.txt")) must not(beADirectory)
      fileToMove.resolve.getParent must not(exist)
      source must exist
    }
  }

  "Copying a file" should {
    "also create any required missing directories" in new fs {
      Files.createDirectories(target)
      val fileToCopy = new FileLocation(source, Paths.get("dir", "copyme.txt"), false)
      Files.createDirectories(fileToCopy.resolve.getParent)
      Files.createFile(fileToCopy.resolve)
      fileUtils.copy(fileToCopy, new FileLocation(target, Paths.get("otherdir", "copiedme.txt"), false))
      target.resolve(Paths.get("otherdir", "copiedme.txt")) must exist
      target.resolve(Paths.get("otherdir", "copiedme.txt")) must not(beADirectory)
      fileToCopy.resolve must exist
    }
  }
}
