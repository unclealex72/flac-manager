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

package uk.co.unclealex.music.common.exception;

import java.nio.file.Path;

/**
 * An execption that is thrown when paths supplied to a command are either not a
 * directory or not relative to the path that command intrinsicly works on.
 * 
 * @author alex
 * 
 */
public class InvalidDirectoriesException extends Exception {

  private final Iterable<Path> invalidDirectories;

  public InvalidDirectoriesException(String message, Iterable<Path> invalidDirectories) {
    super(message);
    this.invalidDirectories = invalidDirectories;
  }

  public Iterable<Path> getInvalidDirectories() {
    return invalidDirectories;
  }
}
