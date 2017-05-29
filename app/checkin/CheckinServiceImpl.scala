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

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import checkin.actors.Messages.{Actions, CompletionNotifier}
import common.message.{MessageService, Messaging}

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * The [[CheckinService]] that delegates to an [[akka.actor.Actor]]
  * @param checkinActor A reference to the actor that will check in the flac files.
  * @param ec An execution context used to fire off checkin actions.
  */
class CheckinServiceImpl @Inject()(@Named("checkin-actor") checkinActor: ActorRef)
                                  (implicit ec: ExecutionContext) extends CheckinService with Messaging {

  /**
    * @inheritdoc
    */
  override def checkin(actions: Seq[Action])(implicit messagingService: MessageService): Future[_] = {
    val promise = Promise[Unit]
    val completionNotifier = new CompletionNotifier {
      override def finished(): Unit = promise.success(())
    }
    checkinActor ! Actions(actions, messagingService, completionNotifier)
    promise.future
  }

}
