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

import java.nio.file.Path

import common.files.{DeviceFileLocation, FileLocation}
import common.message._

import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.util.Try


/**
 * The base class for {@link Synchroniser}s that looks after deciding which
 * files need to be added, kept and removed but delegates the actual adding and
 * removing to its subclasses.
 *
 * @author alex
 */
class SynchronisationManagerImpl(val deviceConnectionService: DeviceConnectionService, val lastModifiedFactory: LastModifiedFactory)
  extends SynchronisationManager with Messaging {

  def synchronise(device: Device, fileLocations: Traversable[DeviceFileLocation])(implicit messageService: MessageService): Try[Unit] = {
    device.beforeMount
    val mountPath = mount(device)
    device.afterMount(mountPath)
    val syncResult = synchroniseFiles(device, fileLocations)
    device.beforeUnmount
    unmount(mountPath)
    device.afterUnmount
    syncResult
  }

  def synchroniseFiles(device: Device, fileLocations: Traversable[DeviceFileLocation])(implicit messageService: MessageService): Try[Unit] = Try {
    def unique[K, V]: Map[K, Traversable[V]] => Map[K, V] = m => m.mapValues(_.find(_ => true).get)
    val deviceFilesByRelativePath = unique(device.listDeviceFiles.groupBy(_.relativePath))
    val fileLocationsByRelativePath = unique(fileLocations.groupBy(_.relativePath.toString))
    val relativePaths = SortedSet[String]() ++ deviceFilesByRelativePath.keys ++ fileLocationsByRelativePath.keys
    val fileActions = relativePaths.map { relativePath =>
      val optionalDeviceFile = deviceFilesByRelativePath.get(relativePath)
      val optionalFileLocation = fileLocationsByRelativePath.get(relativePath)
      (optionalDeviceFile, optionalFileLocation) match {
        case (None, None) => Ignore(relativePath)
        case (Some(deviceFile), None) => Remove(deviceFile)
        case (None, Some(fileLocation)) => Add(fileLocation)
        case (Some(deviceFile), Some(fileLocation)) =>
          if (laterThan(lastModifiedFactory(fileLocation), deviceFile.lastModified)) Add(fileLocation) else Keep(deviceFile)
      }
    }
    fileActions.foreach(_.broadcast)
    fileActions.foreach(_.execute(device))
  }

  def mount(device: Device): Path = {
    deviceConnectionService.mount(device.uuid)
  }

  /**
   * Test if the lhs is later than the rhs but also that there is not exactly an
   * hour's difference.
   *
   * @param lhs
   * the lhs
   * @param rhs
   * the rhs
   * @return true, if successful
   */
  def laterThan(lhs: Long, rhs: Long): Boolean = (Math.abs(lhs - rhs) != 1.hour.toMillis) && lhs > rhs

  /**
   * Unmount the device and remove the mount point if it still exists.
   *
   * @param mountPath
   * The path where the device is mounted.
   * @throws java.io.IOException
   */
  def unmount(mountPath: Path): Unit = {
    deviceConnectionService.unmount(mountPath)
  }
}

sealed trait FileAction extends Messaging {
  def broadcast(implicit messageService: MessageService): Unit

  def execute(device: Device): Unit
}

case class Add(fileLocation: FileLocation) extends FileAction {
  def broadcast(implicit messageService: MessageService) = log(SYNC_ADD(fileLocation))

  def execute(device: Device) = device.add(fileLocation)
}

case class Remove(deviceFile: DeviceFile) extends FileAction {
  def broadcast(implicit messageService: MessageService) = log(SYNC_REMOVE(deviceFile))

  def execute(device: Device) = device.remove(deviceFile)
}

case class Keep(deviceFile: DeviceFile) extends FileAction {
  def broadcast(implicit messageService: MessageService) = log(SYNC_KEEP(deviceFile))

  def execute(device: Device) = {}
}

case class Ignore(relativePath: String) extends FileAction {
  def broadcast(implicit messageService: MessageService) = log(SYNC_IGNORE(relativePath))

  def execute(device: Device) = {}
}

