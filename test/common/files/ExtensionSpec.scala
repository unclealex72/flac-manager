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

import common.files.PathImplicits._
import org.specs2.mutable._

/**
 * Created by alex on 02/11/14.
 */
class ExtensionSpec extends Specification {

  "changing a filename with no dots anywhere except to denote the extension" should {
    "replace the old extension with the new" in {
      Paths.get("a", "b", "cde.flac") withExtension MP3 must beEqualTo(Paths.get("a", "b", "cde.mp3"))
    }
  }

  "changing a filename with no dots anywhere at all" should {
    "return a path equal to the original" in {
      Paths.get("a", "b", "cde") withExtension MP3 must beEqualTo(Paths.get("a", "b", "cde"))
    }
  }

  "changing a filename with two dots" should {
    "replace the old extension with the new" in {
      Paths.get("a", "b", "cde.doit.flac") withExtension MP3 must beEqualTo(Paths.get("a", "b", "cde.doit.mp3"))
    }
  }

  "changing a filename with a dot within a directory and an extension" should {
    "replace the old extension with the new" in {
      Paths.get("a", "b.z", "cde.doit.flac") withExtension MP3 must beEqualTo(Paths.get("a", "b.z", "cde.doit.mp3"))
    }
  }

  "changing a filename with a dot within a directory and no extension" should {
    "return a path equal to the original" in {
      Paths.get("a", "b.z", "cde") withExtension MP3 must beEqualTo(Paths.get("a", "b.z", "cde"))
    }
  }
}
