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

package upnp

import org.fourthline.cling.binding.annotations
import org.fourthline.cling.binding.annotations._
import org.fourthline.cling.model.action.ActionException
import org.fourthline.cling.model.profile.RemoteClientInfo
import org.fourthline.cling.model.types.ErrorCode

/**
  * A Upnp service that advertises the URL where this server can be contacted and the name of the datum filename
  * so that clients can locate repositories.
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
  private var datumFilename: java.lang.String = _

  /**
    * Set the port this server is running on.
    * @param newPortValue The new value of the port.
    * @param remoteClientInfo Information about the remote client used to make sure that this method can
    *                         only be called locally.
    */
  @UpnpAction def setPort(
                           @UpnpInputArgument(name = "NewPortValue") newPortValue: java.lang.Integer,
                           remoteClientInfo: RemoteClientInfo): Unit = {
    localOnly(remoteClientInfo) {
      port = newPortValue
    }
  }

  /**
    * Set the datum filename for this server.
    * @param newDatumFilenameValue The new value of the datum filename.
    * @param remoteClientInfo Information about the remote client used to make sure that this method can
    *                         only be called locally.
    */
  @UpnpAction def setDatumFilename(
                           @UpnpInputArgument(name = "NewDatumFilenameValue") newDatumFilenameValue: java.lang.String,
                           remoteClientInfo: RemoteClientInfo): Unit = {
    localOnly(remoteClientInfo) {
      datumFilename = newDatumFilenameValue
    }
  }

  /**
    * Only allow an action to be executed locally.
    * @param remoteClientInfo Information about the remote client that made this call, if any.
    * @param callback The callback to execute if this call was made by a local client.
    */
  def localOnly(remoteClientInfo: RemoteClientInfo)(callback: =>Unit): Unit = {
    if (remoteClientInfo != null) {
      throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED)
    }
    callback
  }

  /**
    * Get the URL on which this service can be contacted. Information about the remote client is needed so that
    * the correct IP address can be chosen.
    * @param remoteClientInfo Information about the remote client that can be used to work out the external IP address
    *                         of the server.
    * @return The URL on which this service can be contacted.
    */
  @UpnpAction(out = Array(new UpnpOutputArgument(name = "Url")))
  def getUrl(remoteClientInfo: RemoteClientInfo): java.lang.String = {
    s"http:/${remoteClientInfo.getConnection.getLocalAddress}:$port"
  }

  /**
    * Get the name of the datum file.
    * @return The name of the datum file.
    */
  @UpnpAction(out = Array(new UpnpOutputArgument(name = "DatumFilename")))
  def getDatumFilename: java.lang.String = datumFilename
}
