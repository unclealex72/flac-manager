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

import java.io.IOException;
import java.util.Map;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.files.JFileLocation;

/**
 * An interface for classes that check that a list of {@link uk.co.unclealex.music.action.JAction}s will not
 * leave repositories in an invalid state.
 * 
 * @author alex
 * 
 */
public interface JFlacFilesValidator {

  /**
   * Validate a list of {@link uk.co.unclealex.music.action.JAction}s and add {@link uk.co.unclealex.music.action.JFailureAction}s for any
   * failure found.
   * 
   * @param actions
   *          The {@link uk.co.unclealex.music.action.JActions} containing the {@link uk.co.unclealex.music.action.JAction}s to check.
   * @return An {@link uk.co.unclealex.music.action.JActions} object that contains all the original
   *         {@link uk.co.unclealex.music.action.JActions} and also any generated {@link uk.co.unclealex.music.action.JFailureAction}s.
   * @throws IOException 
   */
  public JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) throws IOException;
}
