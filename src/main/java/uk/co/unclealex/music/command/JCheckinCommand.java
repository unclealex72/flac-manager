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
import uk.co.unclealex.music.command.checkin.JCheckinModule;
import uk.co.unclealex.music.command.checkin.process.JMappingService;
import uk.co.unclealex.music.command.inject.JExternalModule;
import uk.co.unclealex.music.command.validation.JFailuresOnly;
import uk.co.unclealex.music.command.validation.JFindMissingCoverArt;
import uk.co.unclealex.music.command.validation.JFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JNoOverwriting;
import uk.co.unclealex.music.command.validation.JNoOwner;
import uk.co.unclealex.music.command.validation.JUnique;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.process.inject.PackageCheckingModule;


/**
 * The main command for checking in files into the FLAC repository.
 * @author alex
 * 
 */
public class JCheckinCommand extends JCommand<JCheckinCommandLine> {

  @Inject
  public JCheckinCommand(
          JExecution<JCheckinCommandLine> execution,
          JActions actions,
          @JNoOwner JFlacFilesValidator noOwnerFlacFilesValidator,
          @JFindMissingCoverArt JFlacFilesValidator findMissingCoverArtFlacFilesValidator,
          @JUnique JFlacFilesValidator uniqueFlacFilesValidator,
          @JNoOverwriting JFlacFilesValidator noOverwritingFlacFilesValidator,
          @JFailuresOnly JFlacFilesValidator failuresOnlyFlacFilesValidator,
          JDirectoryService directoryService,
          JFileLocationFactory fileLocationFactory,
          JMappingService mappingService,
          JActionExecutor actionExecutor) {
    super(execution, actions, Arrays.asList(
        noOwnerFlacFilesValidator,
        findMissingCoverArtFlacFilesValidator,
        uniqueFlacFilesValidator,
        noOverwritingFlacFilesValidator,
        failuresOnlyFlacFilesValidator), directoryService, mappingService, actionExecutor, fileLocationFactory);
  }

  @Override
  @Executable({ JCheckinModule.class, JExternalModule.class, PackageCheckingModule.class })
  public void execute(JCheckinCommandLine commandLine) throws IOException, JInvalidDirectoriesException {
    super.execute(commandLine);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected JFileLocation getRequiredBasePath(JFileLocationFactory fileLocationFactory) {
    return fileLocationFactory.createStagingFileLocation(Paths.get(""));
  }

}