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

package client

import java.io.OutputStream
import java.net.URI

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import io.circe.Json
import play.api.http.Writeable
import play.api.libs.ws.{StreamedResponse, WSClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
  * Connect to the server and run a command remotely.
  * Created by alex on 17/04/17
  **/
object RemoteCommandRunner {

  implicit val jsonWriteable: Writeable[Json] =
    Writeable(json => ByteString(json.noSpaces), Some("application/json"))

  def apply(
             ws: WSClient,
             body: Json,
             serverUri: URI,
             out: OutputStream)
           (implicit materializer: Materializer, executionContext: ExecutionContext): Future[Unit] = {
    val commandUri = serverUri.resolve(new URI(s"/commands"))
    val futureResponse: Future[StreamedResponse] =
      ws.url(commandUri.toString).withRequestTimeout(Duration.Inf).withMethod("POST").withBody(body).stream()
    futureResponse.flatMap { res =>
      val sink = Sink.foreach[ByteString] { bytes =>
        out.write(bytes.toArray)
      }
      res.body.runWith(sink).andThen {
        case result =>
          result.get
      }
    }.map(_ => {})
  }
}
