package checkin

import java.nio.file.Path

import common.commands.CommandService

import scala.sys.process._

/**
 * Created by alex on 16/11/14.
 */
class Mp3EncoderImpl(commandService: CommandService) extends Mp3Encoder {

  override def encode(source: Path, target: Path): Unit = {
    Seq(commandService.flac2mp3Command, source.toString, target.toString) !< ProcessLogger(_ => {})
  }
}
