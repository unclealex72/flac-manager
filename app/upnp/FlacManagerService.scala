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

import org.fourthline.cling.binding.annotations
import org.fourthline.cling.binding.annotations._
import org.fourthline.cling.model.action.ActionException
import org.fourthline.cling.model.profile.RemoteClientInfo
import org.fourthline.cling.model.types.{Datatype, ErrorCode}

/**
  * Created by alex on 17/04/17
  **/
@annotations.UpnpService(
  serviceId = new UpnpServiceId("FlacManagerService"),
  serviceType = new UpnpServiceType(value = "FlacManagerService", version = 1))
@annotations.UpnpStateVariables(
  value = Array(
    new annotations.UpnpStateVariable(
      name = "Port",
      defaultValue = "0",
      sendEvents = false,
      datatype = "i4"),
    new annotations.UpnpStateVariable(
      name = "DatumFilename",
      defaultValue = "",
      sendEvents = false,
      datatype = "string"),
    new annotations.UpnpStateVariable(
      name = "Url",
      defaultValue = "",
      sendEvents = false,
      datatype = "string")
  )
)
class FlacManagerService {

  private var port: java.lang.Integer = _
  private var url: java.lang.String = _
  private var datumFilename: java.lang.String = _

  @UpnpAction def setPort(
                           @UpnpInputArgument(name = "NewPortValue") newPortValue: java.lang.Integer,
                           remoteClientInfo: RemoteClientInfo): Unit = {
    localOnly(remoteClientInfo) {
      port = newPortValue
    }
  }

  @UpnpAction def setDatumFilename(
                           @UpnpInputArgument(name = "NewDatumFilenameValue") newDatumFilenameValue: java.lang.String,
                           remoteClientInfo: RemoteClientInfo): Unit = {
    localOnly(remoteClientInfo) {
      datumFilename = newDatumFilenameValue
    }
  }

  private def localOnly(remoteClientInfo: RemoteClientInfo)(callback: =>Unit): Unit = {
    if (remoteClientInfo != null) {
      throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED)
    }
    callback
  }

  @UpnpAction(out = Array(new UpnpOutputArgument(name = "Url")))
  def getUrl(remoteClientInfo: RemoteClientInfo): java.lang.String = {
    s"http:/${remoteClientInfo.getConnection.getLocalAddress}:$port"
  }

  @UpnpAction(out = Array(new UpnpOutputArgument(name = "DatumFilename")))
  def getDatumFilename: java.lang.String = datumFilename
}
