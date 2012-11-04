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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

package uk.co.unclealex.music.files;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.Directories;

/**
 * @author alex
 * 
 */
public class FileLocationFactoryImpl implements FileLocationFactory {

  /**
   * The {@link Directories} configuration object containing all base
   * directories.
   */
  private final Directories directories;

  @Inject
  public FileLocationFactoryImpl(Directories directories) {
    super();
    this.directories = directories;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FileLocation createFlacFileLocation(Path relativeFlacFile) {
    return resolve(getDirectories().getFlacPath(), relativeFlacFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FileLocation createDeviceFileLocation(String deviceSubDirectory, Path relativeEncodedFile) {
    return resolve(getDirectories().getDevicesPath(), Paths.get(deviceSubDirectory), relativeEncodedFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FileLocation createEncodedFileLocation(Path relativeEncodedFile) {
    return resolve(getDirectories().getEncodedPath(), relativeEncodedFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FileLocation createStagingFileLocation(Path relativeFlacFile) {
    return resolve(getDirectories().getStagingPath(), relativeFlacFile);
  }

  /**
   * Create a {@link FileLocation} from a base path and a list of relative
   * paths.
   * 
   * @param basePath
   *          The base path for the {@link FileLocation}.
   * @param relativePath
   *          The first relative path.
   * @param relativePaths
   *          The other relative paths.
   * @return A {@link FileLocation} with the given base path and calculated
   *         relative path.
   */
  protected FileLocation resolve(Path basePath, Path relativePath, Path... relativePaths) {
    for (Path extraRelativePath : relativePaths) {
      relativePath = relativePath.resolve(extraRelativePath);
    }
    return new FileLocation(basePath, relativePath);
  }

  public Directories getDirectories() {
    return directories;
  }

}
