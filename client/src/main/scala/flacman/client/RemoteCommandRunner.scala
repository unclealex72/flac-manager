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

import java.io.OutputStream
import java.net.URI

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import play.api.libs.ws.{StreamedBody, StreamedResponse, WSClient}
import play.api.mvc.MultipartFormData.DataPart
import play.core.formatters.Multipart

import scala.concurrent.{ExecutionContext, Future}

/**
  * Connect to the server and run a command remotely.
  * Created by alex on 17/04/17
  **/
object RemoteCommandRunner {

  def apply(
             ws: WSClient,
             config: Config,
             bodyParameters: Map[String, String],
             serverUri: URI,
             out: OutputStream)
           (implicit materializer: Materializer, executionContext: ExecutionContext): Future[Unit] = {
    val commandUri = serverUri.resolve(new URI(s"/commands/${config.command.name}"))
    val bodyStream = Source(bodyParameters.map(kv => DataPart(kv._1, kv._2)))
    val boundary = Multipart.randomBoundary()
    val contentType = s"multipart/form-data; boundary=$boundary"
    val body = StreamedBody(Multipart.transform(bodyStream, boundary))
    val futureResponse: Future[StreamedResponse] =
      ws.url(commandUri.toString).withMethod("POST").withBody(body).
        withHeaders("Content-Type" -> contentType).stream()
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
