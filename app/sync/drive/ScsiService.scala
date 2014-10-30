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

package sync.drive

import java.nio.file.Path

/**
 * An interface for classes that can enumerate the SCSI services connected to a
 * machine.
 *
 * @author alex
 *
 */
trait ScsiService {
  /**
   * List the first partition path for each SCSI device connected to the
   * machine. So, for example if SCSI device <i>A</i> is located at
   * <code>/dev/sda</code> and has partitions at <code>/dev/sda1</code> and
   * <code>/dev/sda2</code>, then the partition path is <code>/dev/sda1</code>/
   *
   * @return A map of Linux partition paths keyed by the { @link ScsiId} of the
   *         SCSI device located there.
   */
  def listDevicePathsByScsiIds: Seq[(ScsiId, Path)]
}