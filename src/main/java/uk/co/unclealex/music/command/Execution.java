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

package uk.co.unclealex.music.command;

import java.io.IOException;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.files.FileLocation;

/**
 * The interface for classes that actually contain the execution logic for a
 * command. Instances of this interface will be created by dependency injection.
 * 
 * @author alex
 * 
 */
public interface Execution<C extends CommandLine> {

  /**
   * Create a list of actions that are needed to execute a command on a FLAC
   * file.
   * 
   * @param actions The {@link Action}s already queued for execution.
   * @param flacFileLocation
   *          The location of the FLAC file that is being operated upon.
   * @param musicFile
   *          The tagging information associated with the FLAC file.
   * @return An {@link Actions} object containing all the actions that need to
   *         be executed.
   */
  public Actions execute(C commandLine, Actions actions, FileLocation flacFileLocation, MusicFile musicFile)
      throws IOException;
}
