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

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.JDirectories;

/**
 * @author alex
 * 
 */
public class JFileLocationFactoryImpl implements JFileLocationFactory {

  /**
   * The {@link uk.co.unclealex.music.configuration.JDirectories} configuration object containing all base
   * directories.
   */
  private final JDirectories directories;

  @Inject
  public JFileLocationFactoryImpl(JDirectories directories) {
    super();
    this.directories = directories;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JFileLocation createFlacFileLocation(Path relativeFlacFile) {
    return resolve(true, getDirectories().getFlacPath(), relativeFlacFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JFileLocation createEncodedFileLocation(Path relativeEncodedFile) {
    return resolve(true, getDirectories().getEncodedPath(), relativeEncodedFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JFileLocation createStagingFileLocation(Path relativeFlacFile) {
    return resolve(false, getDirectories().getStagingPath(), relativeFlacFile);
  }

  /**
   * Create a {@link JFileLocation} from a base path and a list of relative
   * paths.
   * 
   * @param readOnly
   *          True if the {@link JFileLocation} is expected to be read only,
   *          false otherwise.
   * @param basePath
   *          The base path for the {@link JFileLocation}.
   * @param relativePath
   *          The first relative path.
   * @param relativePaths
   *          The other relative paths.
   * @return A {@link JFileLocation} with the given base path and calculated
   *         relative path.
   */
  protected JFileLocation resolve(boolean readyOnly, Path basePath, Path relativePath, Path... relativePaths) {
    for (Path extraRelativePath : relativePaths) {
      relativePath = relativePath.resolve(extraRelativePath);
    }
    return new JFileLocation(basePath, relativePath, readyOnly);
  }

  public JDirectories getDirectories() {
    return directories;
  }

}
