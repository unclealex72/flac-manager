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

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionExecutor;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.checkin.process.JMappingService;
import uk.co.unclealex.music.command.validation.JFlacFilesValidator;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;

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
public abstract class JCommand<C extends JCommandLine> {

  /**
   * The {@link JExecution} that shall be run by this command.
   */
  private final JExecution<C> execution;

  /**
   * The {@link uk.co.unclealex.music.action.JActions} object used to hold all actions that need to be
   * executed.
   */
  private final JActions actions;

  /**
   * A list of {@link uk.co.unclealex.music.command.validation.JFlacFilesValidator}s used to validate FLAC files.
   */
  private final List<JFlacFilesValidator> flacFilesValidators;

  /**
   * The {@link uk.co.unclealex.music.files.JDirectoryService} used to garner FLAC files.
   */
  private final JDirectoryService directoryService;

  /**
   * The {@link uk.co.unclealex.music.command.checkin.process.JMappingService} used to map {@link uk.co.unclealex.music.JMusicFile}s to.
   * {@link uk.co.unclealex.music.files.JFileLocation}s.
   */
  private final JMappingService mappingService;

  /**
   * The {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}s.
   */
  private final JFileLocationFactory fileLocationFactory;

  /**
   * The {@link uk.co.unclealex.music.action.JActionExecutor} used to execute any generated {@link uk.co.unclealex.music.action.JAction}s.
   */
  private JActionExecutor actionExecutor;

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
  public JCommand(
          JExecution<C> execution,
          JActions actions,
          List<JFlacFilesValidator> flacFilesValidators,
          JDirectoryService directoryService,
          JMappingService mappingService,
          JActionExecutor actionExecutor,
          JFileLocationFactory fileLocationFactory) {
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
   * Execute an {@link JExecution} command.
   * 
   * @param commandLine
   *          The command line being passed to the command.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws uk.co.unclealex.music.exception.JInvalidDirectoriesException
   *           the invalid directories exception
   */
  public void execute(C commandLine) throws IOException, JInvalidDirectoriesException {
    List<String> flacPaths = commandLine.getFlacPaths();
    Function<String, Path> pathFunction = new Function<String, Path>() {
      public Path apply(String path) {
        return Paths.get(path).toAbsolutePath();
      }
    };
    JFileLocation requiredBasePath = getRequiredBasePath(getFileLocationFactory());
    List<Path> flacDirectories = Lists.newArrayList(Iterables.transform(flacPaths, pathFunction));
    SortedSet<JFileLocation> flacFiles = getDirectoryService().listFiles(requiredBasePath, flacDirectories);
    SortedMap<JFileLocation, JMusicFile> musicFilesByFileLocation = Maps.newTreeMap();
    JActions actions = getMappingService().mapPathsToMusicFiles(getActions(), flacFiles, musicFilesByFileLocation);
    if (actions.get().isEmpty()) {
      JExecution<C> execution = getExecution();
      // Generate a list of actions that need to be executed.
      actions = execution.initialise(actions, commandLine);
      for (Entry<JFileLocation, JMusicFile> entry : musicFilesByFileLocation.entrySet()) {
        actions = execution.execute(actions, entry.getKey(), entry.getValue());
      }
      // Validate all the actions and add any failures.
      for (JFlacFilesValidator flacFilesValidator : getFlacFilesValidators()) {
        actions = flacFilesValidator.validate(musicFilesByFileLocation, actions);
      }
    }
    // Execute all the actions.
    JActionExecutor actionExecutor = getActionExecutor();
    for (JAction action : actions.get()) {
      actionExecutor.execute(action);
    }
  }

  /**
   * Get the base path that all arguments to this command must be relative to.
   * 
   * @param fileLocationFactory
   *          The {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create
   *          {@link uk.co.unclealex.music.files.JFileLocation}s.
   * @return The base path that all arguments to this command must be relative
   *         to
   */
  protected abstract JFileLocation getRequiredBasePath(JFileLocationFactory fileLocationFactory);

  /**
   * Gets the {@link JExecution} that shall be run by this command.
   * 
   * @return the {@link JExecution} that shall be run by this command
   */
  public JExecution<C> getExecution() {
    return execution;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.action.JActions} object used to hold all actions that need to be
   * executed.
   * 
   * @return the {@link uk.co.unclealex.music.action.JActions} object used to hold all actions that need to be
   *         executed
   */
  public JActions getActions() {
    return actions;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JDirectoryService} used to garner FLAC files.
   * 
   * @return the {@link uk.co.unclealex.music.files.JDirectoryService} used to garner FLAC files
   */
  public JDirectoryService getDirectoryService() {
    return directoryService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.command.checkin.process.JMappingService} used to map {@link uk.co.unclealex.music.JMusicFile}s to.
   * 
   * @return the {@link uk.co.unclealex.music.command.checkin.process.JMappingService} used to map {@link uk.co.unclealex.music.JMusicFile}s to
   */
  public JMappingService getMappingService() {
    return mappingService;
  }

  /**
   * Gets the a list of {@link uk.co.unclealex.music.command.validation.JFlacFilesValidator}s used to validate FLAC files.
   * 
   * @return the a list of {@link uk.co.unclealex.music.command.validation.JFlacFilesValidator}s used to validate FLAC
   *         files
   */
  public List<JFlacFilesValidator> getFlacFilesValidators() {
    return flacFilesValidators;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.action.JActionExecutor} used to execute any generated
   * {@link uk.co.unclealex.music.action.JAction}s.
   * 
   * @return the {@link uk.co.unclealex.music.action.JActionExecutor} used to execute any generated
   *         {@link uk.co.unclealex.music.action.JAction}s
   */
  public JActionExecutor getActionExecutor() {
    return actionExecutor;
  }

  /**
   * Sets the {@link uk.co.unclealex.music.action.JActionExecutor} used to execute any generated
   * {@link uk.co.unclealex.music.action.JAction}s.
   * 
   * @param actionExecutor
   *          the new {@link uk.co.unclealex.music.action.JActionExecutor} used to execute any generated
   *          {@link uk.co.unclealex.music.action.JAction}s
   */
  public void setActionExecutor(JActionExecutor actionExecutor) {
    this.actionExecutor = actionExecutor;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}s.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}
   *         s
   */
  public JFileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

}
