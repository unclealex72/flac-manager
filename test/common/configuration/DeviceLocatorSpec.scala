/*
 * Copyright 2014 Alex Jones
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

import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope
import sync.{Device, FilesystemDeviceFactory, IpodDeviceFactory}

import scalaz.Success

/**
 * Created by alex on 20/11/14.
 */
class DeviceLocatorSpec extends Specification with Mockito {

  trait Context extends Scope {
    lazy val ipodDeviceFactory = mock[IpodDeviceFactory]
    lazy val filesystemDeviceFactory = mock[FilesystemDeviceFactory]
    lazy val ipod = mock[Device]
    lazy val usb = mock[Device]
    ipodDeviceFactory.create("freddie", Paths.get("/mnt/anythingbutipod/")) returns ipod
    filesystemDeviceFactory.create("brian", Paths.get("/mnt/home/usb"), "subdir") returns usb
    val deviceLocator = new DeviceLocatorImpl(ipodDeviceFactory, filesystemDeviceFactory)
  }

  "reading an iPod string" should {
    "create an iPod at the correct path" in new Context {
      deviceLocator.device("freddie", "ipod:/mnt/anythingbutipod") must beEqualTo(Success(ipod))
    }
  }

  "reading a usb string" should {
    "create an iPod at the correct path" in new Context {
      deviceLocator.device("brian", "usb:/mnt/home/usb,subdir") must beEqualTo(Success(usb))
    }
  }
}
