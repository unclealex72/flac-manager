import controllers.{Commands, Music}
import org.specs2.mutable.Specification
import play.api.Play
import play.api.test.FakeApplication

/**
 * Created by alex on 20/11/14.
 */
class GlobalSpec extends Specification {

  "The application" should {
    "start and stop" in {
      val app = FakeApplication()
      Play.start(app)
      try {
        Global.getControllerInstance(classOf[Music]) must not(beNull)
        Global.getControllerInstance(classOf[Commands]) must not(beNull)
      }
      finally {
        Play.stop()
      }
    }
  }
}
