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

import com.typesafe.scalalogging.StrictLogging
import common.async.BackgroundExecutionContext
import common.configuration.Directories
import javax.inject.Inject
import org.fourthline.cling.{UpnpService, UpnpServiceImpl}
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Configuration, Mode}

import scala.concurrent.Future

/**
  * An implementation of [[UpnpServer]] that uses [[https://github.com/4thline/cling Cling]]
  * @param directories The location of the various music repositories.
  * @param app The Play application object.
  * @param configuration The Play configuration object.
  * @param lifecycle The Play lifecycle object.
  * @param executionContext An execution context used to start and stop this service.
  */
class ClingUpnpServer @Inject()(
                            directories: Directories,
                            app: Application,
                            configuration: Configuration,
                            lifecycle: ApplicationLifecycle)
                               (implicit executionContext: BackgroundExecutionContext) extends UpnpServer with StrictLogging {

  private val upnpService: UpnpService = new UpnpServiceImpl()
  private val maybePort: Option[Int] = app.mode match {
    case Mode.Prod => configuration.getOptional[Int]("play.server.http.port")
    case _ => None
  }
  private val suffix: String = configuration.getOptional[String]("upnp.suffix").getOrElse("")
  private val port: Int = maybePort.getOrElse(9000)

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
