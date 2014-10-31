package files

import common.files.FlacFileCheckerImpl
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
      checking("one_vision.json") must beFalse
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
