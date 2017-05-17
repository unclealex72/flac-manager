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

;

import java.nio.file.{Files, Path}
import javax.inject.Inject
;

/**
 * The default implementation of [[FlacFileChecker]]. This class checks that files start with flac's magic number.
 * @author alex
 *
 */
class FlacFileCheckerImpl @Inject() extends FlacFileChecker {

  /**
    * 0x664c6143 is the magic number for flac files.
    */
  val MAGIC_NUMBER: Vector[Byte] = Vector[Byte](0x66, 0x4C, 0x61, 0x43)

  /**
    * @inheritdoc
    */
  override def isFlacFile(path: Path): Boolean = {
    val in = Files.newInputStream(path)
    try {
      val buffer: Array[Byte] = new Array[Byte](MAGIC_NUMBER.size)
      in.read(buffer) == MAGIC_NUMBER.size && buffer.toVector.equals(MAGIC_NUMBER)
    }
    finally {
      in.close()
    }
  }
}
