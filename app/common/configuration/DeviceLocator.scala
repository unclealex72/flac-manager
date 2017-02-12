/*
 * Copyright 2015 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.configuration

import java.nio.file.Paths
import javax.inject.Inject

import common.message.MessageService
import sync._

import scalaz.{Failure, Success, ValidationNel}

/**
 * A trait that is used to locate devices.
 * Created by alex on 26/07/15.
 */
trait DeviceLocator {
  def device(user: String, str: String): ValidationNel[String, Device]
}

class DeviceLocatorImpl @Inject()(val ipodDeviceFactory: IpodDeviceFactory, val filesystemDeviceFactory: FilesystemDeviceFactory) extends DeviceLocator {

  private val ipod = "ipod:(.+)".r
  private val usb = "usb:(.+?),(.*)".r

  def device(user: String, str: String): ValidationNel[String, Device] = {
    str match {
      case ipod(mountPoint) => Success(ipodDeviceFactory.create(user, Paths.get(mountPoint)))
      case usb(mountPoint, relativeDirectory) => Success(filesystemDeviceFactory.create(user, Paths.get(mountPoint), relativeDirectory))
      case _ => Failure(s"$str is not a valid device locator").toValidationNel
    }
  }
}