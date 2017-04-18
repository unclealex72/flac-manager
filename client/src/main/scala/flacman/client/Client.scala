/*
 * Copyright 2017 Alex Jones
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

package flacman.client
import java.util.logging.LogManager

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.data.{EitherT, NonEmptyList}
import cats.instances.future._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * The main entry point for all commands. The first argument is expected to be the command name.
  * Created by alex on 18/04/17
  **/
object Client extends App {

  LogManager.getLogManager.reset()

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val isDev: Boolean = !Option(System.getenv("FLAC_DEV")).forall(_.isEmpty)
  if (isDev) {
    println("Running in development mode")
  }
  val ws = WS()
  try {
    val eventualAction = for {
      config <- loadConfig()
      serverDetails <- fetchServerDetails()
      bodyParameters <- createBodyParameters(config, serverDetails)
      _ <- runCommand(config, serverDetails, bodyParameters)
    }  yield {}
    Await.result(eventualAction.value, Duration.Inf) match {
      case Left(messages) => messages.toList.foreach(System.err.println)
      case _ => println("The command completed successfully.")
    }
  }
  finally {
    Try(ws.close())
    Try(system.terminate())
  }

  type Response[T] = EitherT[Future, NonEmptyList[String], T]

  def loadConfig(): Response[Config] = EitherT(Future.successful(Config(args)))

  def fetchServerDetails(): Response[ServerDetails] = EitherT(ServerDetails(isDev))

  def createBodyParameters(config: Config, serverDetails: ServerDetails): Response[Map[String, String]] = EitherT {
    Future.successful(BodyBuilder(config, serverDetails.datumFilename))
  }

  def runCommand(config: Config, serverDetails: ServerDetails, bodyParameters: Map[String, String]): Response[Unit] = {
    EitherT.right(RemoteCommandRunner(ws, config, bodyParameters, serverDetails.uri, System.out))
  }

}
