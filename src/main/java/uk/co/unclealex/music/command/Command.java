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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionExecutor;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.command.validation.FlacFilesValidator;
import uk.co.unclealex.music.configuration.Directories;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * The base class for running actual commands.
 *
 * @param <C> the generic type
 * @author alex
 */
/**
 * @author alex
 * 
 * @param <C>
 */
public abstract class Command<C extends CommandLine> {

  /**
   * The {@link Execution} that shall be run by this command.
   */
  private final Execution execution;

  /**
   * The {@link Actions} object used to hold all actions that need to be
   * executed.
   */
  private final Actions actions;

  /**
   * A list of {@link FlacFilesValidator}s used to validate FLAC files.
   */
  private final List<FlacFilesValidator> flacFilesValidators;

  /**
   * The {@link DirectoryService} used to garner FLAC files.
   */
  private final DirectoryService directoryService;

  /**
   * The {@link Directories} object containing all directory configuration.
   */
  private final Directories directories;

  /**
   * The {@link MappingService} used to map {@link MusicFile}s to
   * {@link FileLocation}s.
   */
  private final MappingService mappingService;

  /**
   * The {@link ActionExecutor} used to execute any generated {@link Action}s.
   */
  private ActionExecutor actionExecutor;

  public Command(
      Execution execution,
      Actions actions,
      List<FlacFilesValidator> flacFilesValidators,
      DirectoryService directoryService,
      Directories directories,
      MappingService mappingService,
      ActionExecutor actionExecutor) {
    super();
    this.execution = execution;
    this.actions = actions;
    this.flacFilesValidators = flacFilesValidators;
    this.directoryService = directoryService;
    this.directories = directories;
    this.mappingService = mappingService;
    this.actionExecutor = actionExecutor;
  }

  /**
   * Execute an {@link Execution} command.
   * 
   * @param commandLine
   *          The command line being passed to the command.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws InvalidDirectoriesException
   *           the invalid directories exception
   */
  public void execute(C commandLine) throws IOException, InvalidDirectoriesException {
    Function<String, Path> pathFunction = new Function<String, Path>() {
      public Path apply(String path) {
        return Paths.get(path);
      }
    };
    SortedSet<FileLocation> flacFiles =
        getDirectoryService().listFiles(
            getRequiredBasePath(getDirectories()),
            Iterables.transform(commandLine.getFlacPaths(), pathFunction));
    SortedMap<FileLocation, MusicFile> musicFilesByFileLocation = Maps.newTreeMap();
    Actions actions = getMappingService().mapPathsToMusicFiles(getActions(), flacFiles, musicFilesByFileLocation);
    if (!actions.get().isEmpty()) {
      Execution execution = getExecution();
      // Generate a list of actions that need to be executed.
      for (Entry<FileLocation, MusicFile> entry : musicFilesByFileLocation.entrySet()) {
        actions = execution.execute(actions, entry.getKey(), entry.getValue());
      }
      // Validate all the actions and add any failures.
      for (FlacFilesValidator flacFilesValidator : getFlacFilesValidators()) {
        actions = flacFilesValidator.validate(musicFilesByFileLocation, actions);
      }
    }
    // Execute all the actions.
    ActionExecutor actionExecutor = getActionExecutor();
    for (Action action : actions.get()) {
      actionExecutor.execute(action);
    }
  }

  /**
   * Get the base path that all arguments to this command must be relative to.
   * 
   * @param directories
   *          The {@link Directories} taken from configuration.
   * @return The base path that all arguments to this command must be relative
   *         to
   */
  protected abstract Path getRequiredBasePath(Directories directories);

  /**
   * Gets the {@link Execution} that shall be run by this command.
   * 
   * @return the {@link Execution} that shall be run by this command
   */
  public Execution getExecution() {
    return execution;
  }

  /**
   * Gets the {@link Actions} object used to hold all actions that need to be
   * executed.
   * 
   * @return the {@link Actions} object used to hold all actions that need to be
   *         executed
   */
  public Actions getActions() {
    return actions;
  }

  /**
   * Gets the {@link DirectoryService} used to garner FLAC files.
   * 
   * @return the {@link DirectoryService} used to garner FLAC files
   */
  public DirectoryService getDirectoryService() {
    return directoryService;
  }

  public Directories getDirectories() {
    return directories;
  }

  public MappingService getMappingService() {
    return mappingService;
  }

  public List<FlacFilesValidator> getFlacFilesValidators() {
    return flacFilesValidators;
  }

  public ActionExecutor getActionExecutor() {
    return actionExecutor;
  }

  public void setActionExecutor(ActionExecutor actionExecutor) {
    this.actionExecutor = actionExecutor;
  }

}
