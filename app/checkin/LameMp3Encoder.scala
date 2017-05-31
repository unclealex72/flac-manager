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

package checkin

import java.nio.file.Path
import javax.inject.Inject

import scala.sys.process._

/**
  * An implementation that uses [[http://lame.sourceforge.net/ Lame]] to encode flac files to MP3.
  */
class LameMp3Encoder @Inject()() extends Mp3Encoder {

  override def encode(source: Path, target: Path): Unit = {
    Seq("flac", "-dcs", source.toString) #| Seq("lame", "--silent", "--resample", "44100", "-h", "-b", "320", "-", target.toString) !
  }
}
