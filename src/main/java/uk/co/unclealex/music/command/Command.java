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

import uk.co.unclealex.music.exception.InvalidDirectoriesException;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * The base class for running actual commands.
 *
 * @param <C> the generic type
 * @author alex
 */
public abstract class Command<C extends CommandLine> {

  /**
   * The {@link Execution} that shall be run by this command.
   */
  private final Execution execution;
  
  /**
   * Instantiates a new command.
   *
   * @param execution the execution
   */
  public Command(Execution execution) {
    super();
    this.execution = execution;
  }


  /**
   * Execute an {@link Execution} command.
   *
   * @param commandLine The command line being passed to the command.
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidDirectoriesException the invalid directories exception
   */
  public void execute(C commandLine) throws IOException, InvalidDirectoriesException {
    Function<String, Path> pathFunction = new Function<String, Path>() {
      public Path apply(String path) {
        return Paths.get(path);
      }
    };
    Iterable<Path> flacPaths = Iterables.transform(commandLine.getFlacPaths(), pathFunction);
  }


  /**
   * Gets the {@link Execution} that shall be run by this command.
   *
   * @return the {@link Execution} that shall be run by this command
   */
  public Execution getExecution() {
    return execution;
  }
  
}
