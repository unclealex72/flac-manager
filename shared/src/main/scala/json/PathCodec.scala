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

package json

import java.nio.file.{FileSystem, Path, Paths}

import io.circe.{Decoder, Encoder}

/**
  * A [[https://circe.github.io/circe/ circe]] encoder and decoder for paths.
  */
object PathCodec {

  /**
    * A [[https://circe.github.io/circe/ circe]] encoder for paths.
    */
  implicit val pathEncoder: Encoder[Path] = Encoder.encodeString.contramap(path => path.toString)

  /**
    * A [[https://circe.github.io/circe/ circe]] decoder for paths.
    */
  implicit def pathDecoder(implicit fs: FileSystem): Decoder[Path] = Decoder.decodeString.map(str => fs.getPath(str))
}
