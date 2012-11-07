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

package uk.co.unclealex.music.command.validation;

import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.collect.Multimap;

/**
 * A {@link FlacFilesValidator} that adds failures if an existing file will be overwritten.
 * @author alex
 *
 */
public class NoOverwritingFlacFilesValidator extends AbstractGeneratedFilesFlacFilesValidator {

  /**
   * {@inheritDoc}
   */
  @Override
  protected Actions validate(
      Map<FileLocation, MusicFile> musicFilesByFlacPath,
      Multimap<FileLocation, FileLocation> targetFileLocationsBySourceFileLocation,
      Actions actions) {
    for (Entry<FileLocation, FileLocation> entry : targetFileLocationsBySourceFileLocation.entries()) {
      FileLocation sourceFileLocation = entry.getKey();
      FileLocation targetFileLocation = entry.getValue();
      if (Files.exists(targetFileLocation.resolve())) {
        actions = actions.fail(sourceFileLocation, "overwrite", targetFileLocation);
      }
    }
    return actions;
  }

}
