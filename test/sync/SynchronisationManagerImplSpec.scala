/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package sync

import java.io.IOException
import java.nio.file.{Paths, Path}
import java.text.SimpleDateFormat

import files.FileLocation
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

/**
 * Created by alex on 29/10/14.
 */
class SynchronisationManagerImplSpec extends Specification with Mockito {

  import Implicits._

  "The synchronisation manager" should {
    "Add a music file that is not on the device" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val fileLocation: FileLocation = FileLocation("/mp3", "b.txt", false)
      val lastModifiedFactory = MapLastModifiedFactory(
        fileLocation -> "05/09/1972 10:15:00")
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      device.listDeviceFiles returns Set()
      synchronisationManager.synchroniseFiles(device, lastModifiedFactory.fileLocations)
      there was one(device).add(fileLocation)
      there were no(device).remove(any[DeviceFile])
    }

    "Remove a device file that no longer exists" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val lastModifiedFactory = MapLastModifiedFactory()
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      val deviceFile: DeviceFile = DeviceFile("a", "a.txt", "05/09/1972 09:12:00")
      device.listDeviceFiles returns Set(deviceFile)
      synchronisationManager.synchroniseFiles(device, lastModifiedFactory.fileLocations)
      there were no(device).add(any[FileLocation])
      there was one(device).remove(deviceFile)
    }

    "Overwrite an outdated device file" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val fileLocation = FileLocation("/a", "a.txt", true)
      val lastModifiedFactory = MapLastModifiedFactory(fileLocation -> "05/09/1973 09:12:00")
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      val deviceFile: DeviceFile = DeviceFile("a", "a.txt", "05/09/1972 09:12:00")
      device.listDeviceFiles returns Set(deviceFile)
      synchronisationManager.synchroniseFiles(device, lastModifiedFactory.fileLocations)
      there was one(device).add(fileLocation)
      there were no(device).remove(any[DeviceFile])
    }

    "Keep a device file that is exactly one hour older than a music file" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val fileLocation = FileLocation("/a", "a.txt", true)
      val lastModifiedFactory = MapLastModifiedFactory(fileLocation -> "05/09/1972 10:12:00")
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      val deviceFile: DeviceFile = DeviceFile("a", "a.txt", "05/09/1972 09:12:00")
      device.listDeviceFiles returns Set(deviceFile)
      synchronisationManager.synchroniseFiles(device, lastModifiedFactory.fileLocations)
      there were no(device).add(any[FileLocation])
      there were no(device).remove(any[DeviceFile])
    }

    "Keep a device file that is exactly one hour newer than a music file" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val fileLocation = FileLocation("/a", "a.txt", true)
      val lastModifiedFactory = MapLastModifiedFactory(fileLocation -> "05/09/1972 09:12:00")
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      val deviceFile: DeviceFile = DeviceFile("a", "a.txt", "05/09/1972 10:12:00")
      device.listDeviceFiles returns Set(deviceFile)
      synchronisationManager.synchroniseFiles(device, lastModifiedFactory.fileLocations)
      there were no(device).add(any[FileLocation])
      there were no(device).remove(any[DeviceFile])
    }

    "Successfully mount and unmount a device after a successful transfer" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val lastModifiedFactory = MapLastModifiedFactory()
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      device.listDeviceFiles returns Set()
      val result = synchronisationManager.synchronise(device, lastModifiedFactory.fileLocations)
      there was one(deviceConnectionService).mount(anyString)
      there was one(device).beforeMount
      there was one(device).afterMount(any[Path])
      there was one(deviceConnectionService).unmount(any[Path])
      there was one(device).beforeUnmount
      there was one(device).afterUnmount
      result must beASuccessfulTry[Unit]
    }

    "Successfully mount and unmount a device after an unsuccessful transfer" in {
      val deviceConnectionService = mock[DeviceConnectionService]
      val lastModifiedFactory = MapLastModifiedFactory()
      val synchronisationManager = new SynchronisationManagerImpl(deviceConnectionService, lastModifiedFactory)
      val device = mock[Device]
      device.listDeviceFiles throws new RuntimeException("D'oh!")
      val result = synchronisationManager.synchronise(device, lastModifiedFactory.fileLocations)
      there was one(deviceConnectionService).mount(anyString)
      there was one(device).beforeMount
      there was one(device).afterMount(any[Path])
      there was one(deviceConnectionService).unmount(any[Path])
      there was one(device).beforeUnmount
      there was one(device).afterUnmount
      result must beAFailedTry[Unit]
      result.failed.get must beAnInstanceOf[RuntimeException]
    }
  }


}

class MapLastModifiedFactory(m: Map[FileLocation, Long]) extends LastModifiedFactory {

  val fileLocations = m.keys

  override def lastModified(fileLocation: FileLocation): Long = m.get(fileLocation).get

}

object MapLastModifiedFactory {

  def apply(es: (FileLocation, Long)*): MapLastModifiedFactory = new MapLastModifiedFactory(Map(es: _*))
}

object Implicits {
  implicit def stringToInstance(str: String): Long = {
    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str).getTime
  }

  implicit def stringToPath(str: String): Path = Paths.get(str)
}