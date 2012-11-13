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
import uk.co.unclealex.music.action.ActionExecutor;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.checkin.CheckinModule;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.command.inject.ExternalModule;
import uk.co.unclealex.music.command.validation.FailuresOnly;
import uk.co.unclealex.music.command.validation.FindMissingCoverArt;
import uk.co.unclealex.music.command.validation.FlacFilesValidator;
import uk.co.unclealex.music.command.validation.NoOverwriting;
import uk.co.unclealex.music.command.validation.NoOwner;
import uk.co.unclealex.music.command.validation.Unique;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.process.inject.PackageCheckingModule;

import com.lexicalscope.jewel.cli.CommandLineInterface;

/**
 * @author alex
 * 
 */
public class CheckinCommand extends Command<CheckinCommandLine> {

  @Inject
  public CheckinCommand(
      Execution execution,
      Actions actions,
      @NoOwner FlacFilesValidator noOwnerFlacFilesValidator,
      @FindMissingCoverArt FlacFilesValidator findMissingCoverArtFlacFilesValidator,
      @Unique FlacFilesValidator uniqueFlacFilesValidator,
      @NoOverwriting FlacFilesValidator noOverwritingFlacFilesValidator,
      @FailuresOnly FlacFilesValidator failuresOnlyFlacFilesValidator,
      DirectoryService directoryService,
      FileLocationFactory fileLocationFactory,
      MappingService mappingService,
      ActionExecutor actionExecutor) {
    super(execution, actions, Arrays.asList(
        noOwnerFlacFilesValidator,
        findMissingCoverArtFlacFilesValidator,
        uniqueFlacFilesValidator,
        noOverwritingFlacFilesValidator,
        failuresOnlyFlacFilesValidator), directoryService, mappingService, actionExecutor, fileLocationFactory);
  }

  @Override
  @Executable({ CheckinModule.class, ExternalModule.class, PackageCheckingModule.class })
  public void execute(CheckinCommandLine commandLine) throws IOException, InvalidDirectoriesException {
    super.execute(commandLine);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected FileLocation getRequiredBasePath(FileLocationFactory fileLocationFactory) {
    return fileLocationFactory.createStagingFileLocation(Paths.get(""));
  }

}

@CommandLineInterface(application = "flacman-checkin")
interface CheckinCommandLine extends CommandLine {
  // Marker interface.
}