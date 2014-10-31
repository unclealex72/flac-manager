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

import java.nio.file.Path

import common.configuration.Directories

/**
 * @author alex
 *
 */
class FileLocationFactoryImpl(val directories: Directories) extends FileLocationFactory {

  override def createFlacFileLocation(relativeFlacFile: Path): FileLocation = {
    resolve(true, directories.flacPath, relativeFlacFile)
  }

  @Override
  override def createEncodedFileLocation(relativeEncodedFile: Path): FileLocation = {
    resolve(true, directories.encodedPath, relativeEncodedFile)
  }

  @Override
  override def createStagingFileLocation(relativeFlacFile: Path): FileLocation = {
    resolve(false, directories.stagingPath, relativeFlacFile)
  }

  /**
   * Create a {@link FileLocation} from a base path and a list of relative
   * paths.
   *
   * @param readOnly
   * True if the { @link FileLocation} is expected to be read only,
   * false otherwise.
   * @param basePath
   * The base path for the { @link FileLocation}.
   * @param relativePath
   * The first relative path.
   * @return A { @link FileLocation} with the given base path and calculated
   *         relative path.
   */
  def resolve(readyOnly: Boolean, basePath: Path, relativePath: Path): FileLocation = {
    new FileLocation(basePath, relativePath, readyOnly)
  }

}
