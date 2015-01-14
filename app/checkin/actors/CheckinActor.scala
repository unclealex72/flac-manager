/*
 * Copyright 2015 Alex Jones
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

package checkin.actors

import akka.actor.Actor
import akka.routing.RoundRobinPool
import akka.util.Timeout
import checkin.actors.Messages.{Completed, EncodeFlacFileLocation, DeleteFileLocation, Actions}
import checkin.{Delete, Encode}
import com.typesafe.scalalogging.StrictLogging
import scaldi.Injector
import scaldi.akka.AkkaInjectable
import akka.pattern.ask

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

class CheckinActor(implicit inj: Injector) extends Actor with AkkaInjectable with StrictLogging {
  val numberOfEncodingActors = inject[Int]('numberOfConcurrentEncoders)
  val encodingProps = injectActorProps[EncodingActor].withRouter(RoundRobinPool(numberOfEncodingActors))
  logger info s"Using $numberOfEncodingActors concurrent encoders"

  override def receive = {
    case Actions(actions, messageService) => {

      val encodingActor = context.actorOf(encodingProps)
      implicit val timeout = Timeout(1.day)

      val futures: Seq[Future[Any]] = actions.map {
        case Delete(stagedFlacFileLocation) => {
          encodingActor ? DeleteFileLocation(stagedFlacFileLocation, messageService)
        }
        case Encode(stagedFileLocation, flacFileLocation, tags, users) => {
          encodingActor ? EncodeFlacFileLocation(stagedFileLocation, flacFileLocation, tags, users, messageService)
        }
      }

      Future.sequence(futures).onComplete { _ =>
        messageService.finish
      }
    }
  }
}
