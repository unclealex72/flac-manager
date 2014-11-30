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

package common.files

import java.io.IOException
import java.nio.file.{Paths, Path}

import com.typesafe.scalalogging.StrictLogging
import common.AbstractStringCellMappingService

/**
 * Created by alex on 06/11/14.
 */
class DirectoryMappingServiceImpl extends DirectoryMappingService with StrictLogging {

  /**
   * Resolve a set of client side directories into a map of server side directories.
   * @param mtab The contents of the client's /etc/mtab file.
   * @param directories
   * @return A map of the original directories to the resolved directories
   */
  override def withMtab(mtab: String): String => Path = {
    val mtabEntries: Seq[(Path, Path)] = new MtabDirectoryMappingService(mtab).generateMap
    val pathBuilder = (path: Path) => {
      val mtabEntry = mtabEntries.find { entry =>
        val clientPath = entry._2
        path.startsWith(clientPath)
      }
      mtabEntry.map(entry => entry._1.resolve(entry._2.relativize(path))).getOrElse(path)
    }
    str => {
      val path = pathBuilder(asPath(str))
      logger.debug(s"Resolved $str to $path")
      path
    }
  }

  def asPath: String => Path = path => Paths.get(path)

  class MtabDirectoryMappingService(mtab: String) extends AbstractStringCellMappingService[Path, Path](0, 1) {

    override def parseKey(key: String): Option[Path] = key.split(':').lastOption.map(asPath)

    override def parseValue(value: String): Option[Path] = Some(value).map(asPath)

    override def generateLines: Traversable[String] = mtab.lines.filter(_.contains(":")).toSeq
  }

}
