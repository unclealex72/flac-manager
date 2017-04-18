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

import java.net.URI

import cats.data.NonEmptyList
import org.fourthline.cling.UpnpServiceImpl
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader
import org.fourthline.cling.model.meta._
import org.fourthline.cling.model.types.{UDADeviceType, UDAServiceId}
import org.fourthline.cling.registry.{DefaultRegistryListener, Registry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

case class ServerDetails(uri: URI, datumFilename: String)

/**
  * Get the datum file and server URL via UPNP.
  * Created by alex on 17/04/17
  **/
object ServerDetails {

  private type Svc = Service[_ <: Device[_ <: DeviceIdentity, _, _], _]
  private type Invocation = ActionInvocation[_ <: Svc]

  def apply(dev: Boolean): Future[Either[NonEmptyList[String], ServerDetails]] = {
    val uriPromise: Promise[URI]  = Promise()
    val datumFilenamePromise: Promise[String]  = Promise()
    val upnpService = new UpnpServiceImpl()
    val identifier = "FlacManagerService" + (if (dev) "Dev" else "")
    val udaType = new UDADeviceType(identifier)
    val serviceId = new UDAServiceId(identifier)
    upnpService.getRegistry.addListener(new DefaultRegistryListener {
      override def remoteDeviceAdded(registry: Registry, device: RemoteDevice): Unit = execute(device)
      override def remoteDeviceUpdated(registry: Registry, device: RemoteDevice): Unit = execute(device)
      def execute(device: RemoteDevice): Unit = {
        Option(device.findService(serviceId)).foreach { service =>
          def executeAction[T](argumentName: String, promise: Promise[T], responseTransformer: String => T): Unit = {
            ServerDetailsHelper.executeGetter(upnpService.getControlPoint, service, argumentName, promise, responseTransformer)
          }
          executeAction("Url", uriPromise, new URI(_))
          executeAction("DatumFilename", datumFilenamePromise, identity)
        }
      }
    })
    upnpService.getControlPoint.search(new UDADeviceTypeHeader(udaType))
    val eventualServerDetails: Future[Either[NonEmptyList[String], ServerDetails]] = for {
      uri <- uriPromise.future
      datumFilename <- datumFilenamePromise.future
    } yield {
      Future { upnpService.shutdown() }
      Right(ServerDetails(uri, datumFilename))
    }
    eventualServerDetails.recover {
      case e: Throwable => Left(NonEmptyList.of(e.getMessage))
    }
  }

}
