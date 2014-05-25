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
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import uk.co.unclealex.executable.Executable;
import uk.co.unclealex.music.action.JActionExecutor;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.checkin.process.JMappingService;
import uk.co.unclealex.music.command.inject.JExternalModule;
import uk.co.unclealex.music.command.own.JOwnModule;
import uk.co.unclealex.music.command.validation.JCommitOwnership;
import uk.co.unclealex.music.command.validation.JFailuresOnly;
import uk.co.unclealex.music.command.validation.JFlacFilesValidator;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.process.inject.PackageCheckingModule;

/**
 * The command for owning or disowning MusicBrainz tracks.
 * 
 * @author alex
 * 
 */
public class JOwnCommand extends JCommand<JOwnCommandLine> {

  @Inject
  public JOwnCommand(
          JExecution<JOwnCommandLine> execution,
          JActions actions,
          @JCommitOwnership JFlacFilesValidator commitOwnershipFlacFilesValidator,
          @JFailuresOnly JFlacFilesValidator failuresOnlyFlacFilesValidator,
          JDirectoryService directoryService,
          JMappingService mappingService,
          JActionExecutor actionExecutor,
          JFileLocationFactory fileLocationFactory) {
    super(
        execution,
        actions,
        Arrays.asList(commitOwnershipFlacFilesValidator, failuresOnlyFlacFilesValidator),
        directoryService,
        mappingService,
        actionExecutor,
        fileLocationFactory);
  }

  @Override
  @Executable({ JOwnModule.class, JExternalModule.class, PackageCheckingModule.class })
  public void execute(JOwnCommandLine commandLine) throws IOException, JInvalidDirectoriesException {
    super.execute(commandLine);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected JFileLocation getRequiredBasePath(JFileLocationFactory fileLocationFactory) {
    return getFileLocationFactory().createStagingFileLocation(Paths.get(""));
  }
}