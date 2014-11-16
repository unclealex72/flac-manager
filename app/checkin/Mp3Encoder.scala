package checkin

import java.nio.file.Path

/**
 * Created by alex on 16/11/14.
 */
trait Mp3Encoder {

  /**
   * Encode a flac file into an MP3 file.
   * @param source
   * @param target
   */
  def encode(source: Path, target: Path)
}
