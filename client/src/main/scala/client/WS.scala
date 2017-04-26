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

import akka.stream.Materializer
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient

/**
  * An object to hold the WsClient used to talk to the server.
  **/
object WS {

  /**
    * Create a new web client.
    * @param materializer The materialiser used to materialise streams.
    * @return A new web service client.
    */
  def apply()(implicit materializer: Materializer): WSClient = AhcWSClient()
}
