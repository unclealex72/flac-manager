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

package common.async

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

/**
  * An execution context configured using Akka.
  *
  * @param context The base name of the Akka execution context. The name of the dispatcher used will
  *                be `context`-context
  * @param actorSystem The actor system that contains the execution context.
  **/
class AkkaExecutionContext(context: String, actorSystem: ActorSystem) extends DelegatingExecutionContext {

  val delegate: ExecutionContext = actorSystem.dispatchers.lookup(s"$context-context")

}
