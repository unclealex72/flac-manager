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

package checkin
import java.nio.file.Path

import common.files.Extension

import scala.sys.process._

/**
  * A base class that use UNIX processes to encode lossy files.
  **/
abstract class NativeProcessLossyEncoder(extension: Extension) extends LossyEncoder {

  /**
    * Encode a flac file into a lossy file.
    *
    * @param source The source flac file.
    * @param target The target lossy file.
    */
  final override def encode(source: Path, target: Path): Unit = {
    Seq("flac", "-dcs", source.toString) #| createEncodingCommand(target.toAbsolutePath.toString) !
  }

  final override def encodesTo: Extension = extension

  /**
    * The command used to take input from stdin and create a lossy file.
    * @param targetPath
    * @return
    */
  def createEncodingCommand(targetPath: String): Seq[String]
}
