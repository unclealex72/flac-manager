/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author unclealex72
 *
 */

package common.files

;

import java.io.{FileInputStream, InputStream}
import java.nio.file.Path

import scala.util.Try
;

/**
 * The default implementation of {@link FlacFileChecker}.
 * @author alex
 *
 */
class FlacFileCheckerImpl extends FlacFileChecker {

  val MAGIC_NUMBER = Vector[Byte](0x66, 0x4C, 0x61, 0x43)

  /**
   * Check whether a file is a FLAC encoded file or not.
   * @param path The file to check.
   * @return True if the file is a FLAC file or false otherwise.
   * @throws IOException
   */
  override def isFlacFile(path: Path): Boolean = {
    val in = new FileInputStream(path.toFile)
    try {
      isFlacFile(in)
    }
    finally {
      in.close
    }
  }

  def isFlacFile(in: InputStream): Boolean = {
    val buffer: Array[Byte] = new Array[Byte](MAGIC_NUMBER.size)
    in.read(buffer) == MAGIC_NUMBER.size && buffer.toVector.equals(MAGIC_NUMBER)
  }
}
