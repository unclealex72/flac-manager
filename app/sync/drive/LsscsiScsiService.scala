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

import java.nio.file.DirectoryStream.Filter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import scala.collection.SortedSet
import scala.sys.process._
import scala.collection.JavaConversions._

/**
 * An implementation of {@link ScsiService} that uses the Linux command
 * <code>lsscsi</code> to interrogate connected SCSI devices.
 *
 * @author alex
 *
 */
class LsscsiScsiService extends AbstractStringCellMappingService[ScsiId, Path](0, -1) with ScsiService {

  def listDevicePathsByScsiIds = generateMap

  def parseKey(key: String): Option[ScsiId] = Some(ScsiId(key))

  def parseValue(value: String): Option[Path] = {
    val scsiDevicePath: Path = Paths.get(value)
    val deviceNameRegex: String = scsiDevicePath.getFileName.toString + "[0-9]+"
    val f: Filter[Path] = new Filter[Path] {
      def accept(entry: Path): Boolean = {
        return entry.getFileName.toString.matches(deviceNameRegex)
      }
    }
    val partitionPaths = Files.newDirectoryStream(scsiDevicePath.getParent, f).toSeq.sortBy(_.toString)
    partitionPaths.headOption
  }

  /**
   * {@inheritDoc}
   */
  def generateLines: Seq[String] = "lsscsi".lineStream

}