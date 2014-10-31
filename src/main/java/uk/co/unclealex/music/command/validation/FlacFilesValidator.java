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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.files.FileLocation;

/**
 * An interface for classes that check that a list of {@link Action}s will not
 * leave repositories in an invalid state.
 * 
 * @author alex
 * 
 */
public interface FlacFilesValidator {

  /**
   * Validate a list of {@link Action}s and add {@link FailureAction}s for any
   * failure found.
   * 
   * @param actions
   *          The {@link Actions} containing the {@link Action}s to check.
   * @return An {@link Actions} object that contains all the original
   *         {@link Actions} and also any generated {@link FailureAction}s.
   * @throws IOException 
   */
  public Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) throws IOException;
}
