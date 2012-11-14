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
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The base class for running actual commands.
 * 
 * @param <C>
 *          the generic type
 * @author alex
 */
public abstract class Command<C extends CommandLine> {

  /**
   * The {@link Execution} that shall be run by this command.
   */
  private final Execution<C> execution;

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
   * The {@link MappingService} used to map {@link MusicFile}s to.
   * {@link FileLocation}s.
   */
  private final MappingService mappingService;

  /**
   * The {@link FileLocationFactory} used to create {@link FileLocation}s.
   */
  private final FileLocationFactory fileLocationFactory;

  /**
   * The {@link ActionExecutor} used to execute any generated {@link Action}s.
   */
  private ActionExecutor actionExecutor;

  /**
   * Instantiates a new command.
   * 
   * @param execution
   *          the execution
   * @param actions
   *          the actions
   * @param flacFilesValidators
   *          the flac files validators
   * @param directoryService
   *          the directory service
   * @param mappingService
   *          the mapping service
   * @param actionExecutor
   *          the action executor
   * @param fileLocationFactory
   *          the file location factory
   */
  public Command(
      Execution<C> execution,
      Actions actions,
      List<FlacFilesValidator> flacFilesValidators,
      DirectoryService directoryService,
      MappingService mappingService,
      ActionExecutor actionExecutor,
      FileLocationFactory fileLocationFactory) {
    super();
    this.execution = execution;
    this.actions = actions;
    this.flacFilesValidators = flacFilesValidators;
    this.directoryService = directoryService;
    this.mappingService = mappingService;
    this.actionExecutor = actionExecutor;
    this.fileLocationFactory = fileLocationFactory;
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
    List<String> flacPaths = commandLine.getFlacPaths();
    Function<String, Path> pathFunction = new Function<String, Path>() {
      public Path apply(String path) {
        return Paths.get(path);
      }
    };
    FileLocation requiredBasePath = getRequiredBasePath(getFileLocationFactory());
    List<Path> flacDirectories = Lists.newArrayList(Iterables.transform(flacPaths, pathFunction));
    SortedSet<FileLocation> flacFiles = getDirectoryService().listFiles(requiredBasePath, flacDirectories);
    SortedMap<FileLocation, MusicFile> musicFilesByFileLocation = Maps.newTreeMap();
    Actions actions = getMappingService().mapPathsToMusicFiles(getActions(), flacFiles, musicFilesByFileLocation);
    if (actions.get().isEmpty()) {
      Execution<C> execution = getExecution();
      // Generate a list of actions that need to be executed.
      for (Entry<FileLocation, MusicFile> entry : musicFilesByFileLocation.entrySet()) {
        actions = execution.execute(commandLine, actions, entry.getKey(), entry.getValue());
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
   * @param fileLocationFactory
   *          The {@link FileLocationFactory} used to create
   *          {@link FileLocation}s.
   * @return The base path that all arguments to this command must be relative
   *         to
   */
  protected abstract FileLocation getRequiredBasePath(FileLocationFactory fileLocationFactory);

  /**
   * Gets the {@link Execution} that shall be run by this command.
   * 
   * @return the {@link Execution} that shall be run by this command
   */
  public Execution<C> getExecution() {
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

  /**
   * Gets the {@link MappingService} used to map {@link MusicFile}s to.
   * 
   * @return the {@link MappingService} used to map {@link MusicFile}s to
   */
  public MappingService getMappingService() {
    return mappingService;
  }

  /**
   * Gets the a list of {@link FlacFilesValidator}s used to validate FLAC files.
   * 
   * @return the a list of {@link FlacFilesValidator}s used to validate FLAC
   *         files
   */
  public List<FlacFilesValidator> getFlacFilesValidators() {
    return flacFilesValidators;
  }

  /**
   * Gets the {@link ActionExecutor} used to execute any generated
   * {@link Action}s.
   * 
   * @return the {@link ActionExecutor} used to execute any generated
   *         {@link Action}s
   */
  public ActionExecutor getActionExecutor() {
    return actionExecutor;
  }

  /**
   * Sets the {@link ActionExecutor} used to execute any generated
   * {@link Action}s.
   * 
   * @param actionExecutor
   *          the new {@link ActionExecutor} used to execute any generated
   *          {@link Action}s
   */
  public void setActionExecutor(ActionExecutor actionExecutor) {
    this.actionExecutor = actionExecutor;
  }

  /**
   * Gets the {@link FileLocationFactory} used to create {@link FileLocation}s.
   * 
   * @return the {@link FileLocationFactory} used to create {@link FileLocation}
   *         s
   */
  public FileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

}
