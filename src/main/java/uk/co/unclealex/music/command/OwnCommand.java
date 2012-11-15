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

import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import uk.co.unclealex.music.action.ActionExecutor;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.command.validation.CommitOwnership;
import uk.co.unclealex.music.command.validation.FailuresOnly;
import uk.co.unclealex.music.command.validation.FlacFilesValidator;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;

/**
 * The command for owning or disowning MusicBrainz tracks.
 * 
 * @author alex
 * 
 */
public class OwnCommand extends Command<OwnCommandLine> {

  @Inject
  public OwnCommand(
      Execution<OwnCommandLine> execution,
      Actions actions,
      @CommitOwnership FlacFilesValidator commitOwnershipFlacFilesValidator,
      @FailuresOnly FlacFilesValidator failuresOnlyFlacFilesValidator,
      DirectoryService directoryService,
      MappingService mappingService,
      ActionExecutor actionExecutor,
      FileLocationFactory fileLocationFactory) {
    super(
        execution,
        actions,
        Arrays.asList(commitOwnershipFlacFilesValidator, failuresOnlyFlacFilesValidator),
        directoryService,
        mappingService,
        actionExecutor,
        fileLocationFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FileLocation getRequiredBasePath(FileLocationFactory fileLocationFactory) {
    return getFileLocationFactory().createStagingFileLocation(Paths.get(""));
  }
}