package modules

import checkin._
import com.google.inject.AbstractModule
import logging.ApplicationLogging
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Dependency injection for the checkin command.
  */
class CheckinModule extends AbstractModule with ScalaModule with AkkaGuiceSupport with ApplicationLogging {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[CheckinService].to[CheckinServiceImpl].asEagerSingleton()
    bind[Seq[LossyEncoder]].toInstance(Seq(new FdkaacM4AEncoder, new LameMp3Encoder))
    bind[CheckinActionGenerator].to[CheckinActionGeneratorImpl].asEagerSingleton()
    bind[SingleCheckinService].to[SingleCheckinServiceImpl].asEagerSingleton()
  }
}

