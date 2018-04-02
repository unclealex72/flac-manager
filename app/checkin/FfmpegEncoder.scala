/*
 * Copyright 2018 Alex Jones
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
import java.nio.file.{Files, Path}

import checkin.FfmpegEncoder.{EncoderOption, EncoderOptions, _}
import com.typesafe.scalalogging.StrictLogging
import common.files.Extension

import scala.sys.process._

/**
  * A base class that use UNIX ffmpeg processes to encode lossy files.
  **/
class FfmpegEncoder(extension: Extension, override val copiesTags: Boolean, library: String, opts: EncoderOption*) extends LossyEncoder with StrictLogging {

  val logLevel: String = "quiet"

  private final val processLogger: ProcessLogger = ProcessLogger(msg => logger.debug(msg), msg => logger.error(msg))

  /**
    * Encode a flac file into a lossy file.
    *
    * @param source The source flac file.
    * @param target The target lossy file.
    */
  final override def encode(source: Path, target: Path): Int = {
    Files.deleteIfExists(target)
    implicit val pathToString: Path => String = _.toAbsolutePath.toString
    val allOpts: EncoderOptions =
      "y".k +
        "i" ~> source +
        "vn".k ++
        EncoderOptions(opts) +
        "loglevel" ~> logLevel +
        "c:a" ~> library +
        "b:a" ~> "320k" +
        target.v
    val cmd: Seq[String] = "ffmpeg" +: allOpts.toOptions
    val fullCommand: String = cmd.mkString(" ")
    logger.debug(fullCommand)
    val result: Int = cmd.!(processLogger)
    if (result != 0) {
      logger.error(s"Command '$fullCommand' exited with error code $result")
    }
    result
  }

  final override val encodesTo: Extension = extension

}

/**
  * Syntactic sugar for defining options to ffmpeg.
  */
object FfmpegEncoder {

  case class EncoderOption(maybeKey: Option[String], maybeValue: Option[String]) {
    def +(encoderOption: EncoderOption): EncoderOptions = EncoderOptions(Seq(this)) + encoderOption
    def ++(encoderOptions: EncoderOptions): EncoderOptions = EncoderOptions(Seq(this)) ++ encoderOptions

    def toOptions: Seq[String] = maybeKey.map(key => s"-$key").toSeq ++ maybeValue.toSeq
  }

  implicit class KeyToEncoderOption(key: String) {
    def ~>[E](value: E)(implicit conv: E => String): EncoderOption = EncoderOption(Some(key), Some(conv(value)))
    def k : EncoderOption = EncoderOption(Some(key), None)
  }

  implicit class ValueToEncoderOption[E](value: E) {
    def v(implicit conv: E => String): EncoderOption = EncoderOption(None, Some(conv(value)))
  }

  case class EncoderOptions(encoderOptions: Seq[EncoderOption]) {
    def +(encoderOption: EncoderOption): EncoderOptions = EncoderOptions(encoderOptions :+ encoderOption)
    def ++(moreEncoderOptions: EncoderOptions): EncoderOptions = EncoderOptions(encoderOptions ++ moreEncoderOptions.encoderOptions)

    def toOptions: Seq[String] = encoderOptions.flatMap(_.toOptions)
  }
}