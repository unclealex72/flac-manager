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

import java.net.URI

import com.typesafe.scalalogging.StrictLogging
import org.fourthline.cling.binding.annotations
import org.fourthline.cling.binding.annotations.{AnnotationLocalServiceBinder, _}
import org.fourthline.cling.model.meta._
import org.fourthline.cling.model.types.{UDADeviceType, UDN}
import org.fourthline.cling.{UpnpService, UpnpServiceImpl}
import play.api.{Application, Mode}
/**
  * Created by alex on 21/03/17
  * Advertise the flac manager using uPNP.
  **/
object UpnpServer extends StrictLogging {

  val upnpService: UpnpService = new UpnpServiceImpl()

  def start(app: Application): Unit = {
    val thread = new Thread {
      override def run() {
        val (maybePort, host, suffix) = app.mode match {
          case Mode.Prod => (app.configuration.getInt("play.server.http.port"), MyIpAddress(_ => false), "")
          case m => (None, MyIpAddress(nm => nm.contains("docker")), s"-$m")
        }
        upnpService.getRegistry.addDevice(createDevice(host, maybePort.getOrElse(9000), suffix))
      }
    }
    thread.setDaemon(false)
    thread.start()
  }

  def shutdown(): Unit = {
    try {
      logger.info("Shutting down Upnp service")
      upnpService.shutdown()
    }
    catch {
      case e: Exception => logger.error("Could not shutdown the Upnp service.", e)
    }
  }

  def createDevice(host: String, port: Int, suffix: String): LocalDevice = {
    val identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Flac Manager"))
    val deviceType = new UDADeviceType("FlacManager" + suffix, 1)
    val presentationURI = new URI(s"http://$host:$port/")
    val deviceDetails = new DeviceDetails(
      "Flac Manager",
      new ManufacturerDetails("Alex Jones"),
      new ModelDetails("FlacManager" + suffix, "Flac Manager Server", "v1"),
      presentationURI)
    val icon = new Icon("image/png", 48, 48, 8, classOf[FlacManager].getClassLoader.getResource("upnp/icon.png"))
    val flacManagerService = new AnnotationLocalServiceBinder().read(classOf[FlacManager])
    new LocalDevice(identity, deviceType, deviceDetails, icon, flacManagerService)
  }

  @annotations.UpnpService(
    serviceId = new UpnpServiceId("FlacManager"),
    serviceType = new UpnpServiceType(value = "FlacManager", version = 1))
  class FlacManager {
    //noinspection ScalaUnusedSymbol
    @annotations.UpnpStateVariable(defaultValue = "0", sendEvents = false, name = "dummy")
    private var dummy: Boolean = false
  }
}
