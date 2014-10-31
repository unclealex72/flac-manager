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

import common.files.FileLocation

import scala.util.Try

/**
 * Created by alex on 28/10/14.
 */
trait Device {

  /**
   * The unique disk UUID of this device.
   */
  val uuid: String

  /**
   * Subclasses need to override this method to contain any device based logic
   * that needs to be executed before the device is mounted.
   *
   * @throws java.io.IOException
   */
  def beforeMount: Unit

  /**
   * Subclasses need to override this method to contain any device based logic
   * that needs to be executed after the device is mounted but before any file
   * searching or copying takes place.
   *
   * @param mountPath
   * The path where the device has been mounted.
   * @throws java.io.IOException
   */
  def afterMount(mountPath: Path): Unit

  /**
   * List all the files currently on the device.
   *
   * @return A set of { @link DeviceFile}s that represent the files currently on
   *                          the device.
   * @throws java.io.IOException
   * Signals that an I/O exception has occurred.
   */
  def listDeviceFiles: Set[DeviceFile]

  /**
   * Remove a file from the device.
   *
   * @param deviceFile
   * The device file to remove.
   * @throws java.io.IOException
   * Signals that an I/O exception has occurred.
   */
  def remove(deviceFile: DeviceFile): Unit

  /**
   * Add a new file to the device.
   *
   * @param fileLocation
   * The location of the file to add.
   * @throws java.io.IOException
   * Signals that an I/O exception has occurred.
   */
  def add(fileLocation: FileLocation): Unit

  /**
   * Subclasses need to override this method to include any device logic that
   * needs to be executed after the device has been synchronised but before the
   * device is unmounted.
   *
   * @throws java.io.IOException
   */
  def beforeUnmount: Unit

  /**
   * Subclasses need to extend this method to include any logic that needs to be
   * executed after the device has been unmounted.
   *
   * @throws java.io.IOException
   */
  def afterUnmount: Unit

}
