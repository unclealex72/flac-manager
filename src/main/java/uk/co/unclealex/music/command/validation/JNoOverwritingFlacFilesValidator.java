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

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.collect.Multimap;

/**
 * A {@link JFlacFilesValidator} that adds failures if an existing file will be overwritten.
 * @author alex
 *
 */
public class JNoOverwritingFlacFilesValidator extends JAbstractGeneratedFilesFlacFilesValidator {

  /**
   * {@inheritDoc}
   */
  @Override
  protected JActions validate(
      Map<JFileLocation, JMusicFile> musicFilesByFlacPath,
      Multimap<JFileLocation, JFileLocation> targetFileLocationsBySourceFileLocation,
      JActions actions) {
    for (Entry<JFileLocation, JFileLocation> entry : targetFileLocationsBySourceFileLocation.entries()) {
      JFileLocation sourceFileLocation = entry.getKey();
      JFileLocation targetFileLocation = entry.getValue();
      if (Files.exists(targetFileLocation.resolve())) {
        actions = actions.fail(sourceFileLocation, JMessageService.OVERWRITE, targetFileLocation);
      }
    }
    return actions;
  }

}
