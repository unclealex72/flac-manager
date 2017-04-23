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

import org.specs2.mutable._

/**
 * Created by alex on 23/10/14.
 */
class FlacFileCheckerImplSpec extends Specification {

  "The FlaFileCheckerImpl" should {
    "correctly identify a FLAC file" in {
      checking("untagged.flac") must beTrue
    }

    "correctly identify a non-FLAC file" in {
      checking("cover.jpg") must beFalse
    }

    "correctly identify an empty file" in {
      checking("root.txt") must beFalse
    }

  }

  def checking(resourceName: String): Boolean = {
    val in = classOf[FlacFileCheckerImplSpec].getClassLoader.getResourceAsStream(resourceName)
    new FlacFileCheckerImpl().isFlacFile(in)
  }
}
