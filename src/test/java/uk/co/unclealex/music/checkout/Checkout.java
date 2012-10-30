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

package uk.co.unclealex.music.checkout;

import java.io.IOException;
import java.nio.file.Path;

import uk.co.unclealex.music.common.exception.InvalidDirectoriesException;

/**
 * An interface for classes that contain the checkout logic.
 * 
 * @author alex
 * 
 */
public interface Checkout {

  /**
   * Checkout a list of flac files by moving them into the staging directory and
   * also removing any related lossy encoded files.
   * 
   * @param directories A list of directories whose flac files will be checked out.
   * @throws IOException
   */
  public void checkout(Iterable<Path> directories) throws IOException, InvalidDirectoriesException;
}
