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

package upnp

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import common.configuration.Directories
import org.fourthline.cling.{UpnpService, UpnpServiceImpl}
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Configuration, Mode}

import scala.concurrent.{ExecutionContext, Future}
/**
  * Created by alex on 21/03/17
  * Advertise the flac manager using uPNP.
  **/
class ClingUpnpServer @Inject()(
                            directories: Directories,
                            app: Application,
                            configuration: Configuration,
                            lifecycle: ApplicationLifecycle)
                               (implicit ec: ExecutionContext) extends UpnpServer with StrictLogging {

  val upnpService: UpnpService = new UpnpServiceImpl()
  val (maybePort, suffix) = app.mode match {
    case Mode.Prod => (configuration.getInt("play.server.http.port"), "")
    case m => (None, s"$m")
  }
  val port: Int = maybePort.getOrElse(9000)

  new UpnpDeviceCreator(suffix, port, directories.datumPath, upnpService).call()

  lifecycle.addStopHook { () =>
    Future {
      try {
        logger.info("Shutting down Upnp service")
        upnpService.shutdown()
      }
      catch {
        case e: Exception => logger.warn("Could not shutdown the Upnp service.", e)
      }
    }
  }
}
