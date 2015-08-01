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

import java.nio.file.Paths

import common.configuration.{TestDirectories, User}
import org.specs2.mutable._

/**
 * Created by alex on 08/11/14.
 */
class FileLocationSpec extends Specification {

  val (stagingPath, flacPath, encodedPath, devicesPath, tempPath) =
    (Paths.get("/path", "staging"), Paths.get("/path", "flac"), Paths.get("/path", "encoded"), Paths.get("/path", "devices"), Paths.get("/path", "temp"))
  implicit val directories = TestDirectories(flacPath, encodedPath, devicesPath, stagingPath, tempPath)

  "Pattern matching using StagedFlacFileLocation" should {
    "match a valid staging path" in {
      val result = stagingPath.resolve("hello") match {
        case StagedFlacFileLocation(fileLocation) => Some(fileLocation)
        case _ => None
      }
      result must beSome(StagedFlacFileLocation("hello"))
    }
    "not match an invalid staging path" in {
      val result = devicesPath.resolve("hello") match {
        case StagedFlacFileLocation(fileLocation) => Some(fileLocation)
        case _ => None
      }
      result must beNone
    }
  }

  "Pattern matching using FlacFileLocation" should {
    "match a valid flac path" in {
      val result = flacPath.resolve("hello") match {
        case FlacFileLocation(fileLocation) => Some(fileLocation)
        case _ => None
      }
      result must beSome(FlacFileLocation("hello"))
    }
    "not match an invalid staging path" in {
      val result = devicesPath.resolve("hello") match {
        case FlacFileLocation(fileLocation) => Some(fileLocation)
        case _ => None
      }
      result must beNone
    }
  }

  "Trying to identify the root path of a user's device repository" should {
    "correctly resolve to the base of the repository" in {
      val result = DeviceFileLocation(User("brian", "", "", Seq.empty))
      result.toPath must be equalTo (directories.devicesPath.resolve("brian"))
    }
  }
}
