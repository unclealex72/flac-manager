package modules

import akka.routing.RoundRobinPool
import checkin.actors.{CheckinActor, EncodingActor}
import checkin.{CheckinService, CheckinServiceImpl, Mp3Encoder, Mp3EncoderImpl}
import com.google.inject.AbstractModule
import logging.ApplicationLogging
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

class CheckinModule extends AbstractModule with ScalaModule with AkkaGuiceSupport with ApplicationLogging {
  override def configure(): Unit = {
    bind[CheckinService].to[CheckinServiceImpl].asEagerSingleton()
    bind[Mp3Encoder].to[Mp3EncoderImpl].asEagerSingleton()
    // Actors
    bindActor[CheckinActor]("checkin-actor")
    val numberOfConcurrentEncoders = Runtime.getRuntime.availableProcessors()
    logger.info(s"Using $numberOfConcurrentEncoders encoders")
    bindActor[EncodingActor]("encoding-actor", RoundRobinPool(numberOfConcurrentEncoders).props)
  }
}

