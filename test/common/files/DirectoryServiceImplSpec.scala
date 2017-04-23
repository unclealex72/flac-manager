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

import java.nio.file.{Files, Path, Paths}

import common.configuration.TestDirectories
import common.files.FileLocationImplicits._
import common.message.{MessageService, MessageType}
import org.specs2.mock._
import org.specs2.mutable._
import tempfs.TempFileSystem

import scala.collection.SortedSet
import  common.message.NoOpMessageService._

/**
 * @author alex
 *
 */
class DirectoryServiceImplSpec extends Specification with Mockito {

  trait fs extends TempFileSystem {

    implicit val directories = TestDirectories(rootDirectory, rootDirectory.resolve(".datum"))
    implicit val fileLocationExtensions = new FileLocationExtensionsImpl
    def fl(path: String, paths: String*): FlacFileLocation = FlacFileLocation(path, paths: _*)

    implicit val directoryService = new DirectoryServiceImpl

    def before(rootDirectory: Path): Unit = {
      val paths = Seq(
        Paths.get("dir.flac", "myfile.flac"),
        Paths.get("dir.flac", "myfile.xml"),
        Paths.get("dir.flac", "inner", "myfile.flac"),
        Paths.get("dir.flac", "inner", "myfile.xml"),
        Paths.get("my.flac"),
        Paths.get("my.xml"),
        Paths.get("dir", "your.flac"),
        Paths.get("dir", "your.mp3"))
      paths.foreach { path =>
        val fullPath = rootDirectory.resolve("flac").resolve(path)
        Files.createDirectories(fullPath.getParent)
        Files.createFile(fullPath)
      }

    }
  }

  "grouping files in valid directories" should {
    "list the files" in new fs {
      val fileLocations = directoryService.groupFiles(Seq(FlacFileLocation("dir.flac"), FlacFileLocation("dir")))
      fileLocations.toSeq must contain(exactly(
        fl("dir.flac") -> SortedSet(fl("dir.flac", "myfile.flac"), fl("dir.flac", "myfile.xml")),
        fl("dir.flac", "inner") -> SortedSet(fl("dir.flac", "inner", "myfile.flac"), fl("dir.flac", "inner", "myfile.xml")),
        fl("dir") -> SortedSet(fl("dir", "your.flac"), fl("dir", "your.mp3"))
      ))
    }
  }

}
