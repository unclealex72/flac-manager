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

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

import common.AbstractStringCellMappingService

import scala.collection.JavaConversions._

/**
 * An instance of {@link MountedDriveService} that uses <code>/etc/mtab</code>
 * to find which drives are mounted.
 *
 * @author alex
 *
 */
class MtabMountedDriveService extends AbstractStringCellMappingService[Path, Path](1, 0) with MountedDriveService {

  def listDevicesByMountPoint = generateMap

  def parseKey(key: String): Option[Path] = parse(key)

  def parseValue(value: String): Option[Path] = parse(value)

  /**
   * Parse a cell, unescaping all octets.
   *
   * @param cell
   * The cell to parse.
   * @return The path the cell represents or null if this cell does not
   *         represent an absolute path.
   */
  def parse(cell: String): Option[Path] = {
    val nonOctalCell: String = processOctal(cell)
    if (!nonOctalCell.startsWith("/")) None else Some(Paths.get(nonOctalCell))
  }

  /**
   * Convert octal escaped characters in strings.
   *
   * @param line
   * The line to convert.
   * @return A new string that contains normal characters
   */
  protected def processOctal(line: String): String = {
    """\\([0-7]{3})""".r.replaceAllIn(line, m => Integer.parseInt(m.group(1), 8).toChar.toString)
  }

  /**
   * {@inheritDoc}
   */
  def generateLines: Seq[String] = Files.readAllLines(Paths.get("/etc", "mtab"), Charset.defaultCharset)
}