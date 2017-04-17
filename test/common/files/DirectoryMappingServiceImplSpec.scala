/*
 * Copyright 2017 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.files

import java.nio.file.Path

import common.configuration.TestDirectories
import org.specs2.mutable.Specification

/**
  * Created by alex on 17/04/17
  **/
class DirectoryMappingServiceImplSpec extends Specification {

  val directories: TestDirectories = TestDirectories(datum = "/somewhere/music/.datum")

  val directoryMappingService: DirectoryMappingServiceImpl = new DirectoryMappingServiceImpl(directories)
  val pathMapper: String => String = directoryMappingService.withDatumFileLocation("/mnt/music/.datum")(_).toString

  "A file resolvable from the datum file " should {
    "return the absolute path of where it is on the server" in {
      pathMapper("/mnt/music/flac/boogie") must be_==("/somewhere/music/flac/boogie")
    }
  }

  "A file not resolvable from the datum file " should {
    "return the absolute path of where it is on the client" in {
      pathMapper("/mnt/videos/films/boogie") must be_==("/mnt/videos/films/boogie")
    }
  }
}
