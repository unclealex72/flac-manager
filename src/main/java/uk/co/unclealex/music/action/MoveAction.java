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

package uk.co.unclealex.music.action;

import java.io.IOException;

import uk.co.unclealex.music.common.files.FileLocation;

/**
 * An action used to move a file.
 * 
 * @author alex
 * 
 */
public class MoveAction extends AbstractAction implements Action {

  /**
   * The location of where to move the file.
   */
  private final FileLocation targetFileLocation;

  /**
   * Instantiates a new move action.
   *
   * @param fileLocation the source location
   * @param targetFileLocation the target location
   */
  public MoveAction(FileLocation fileLocation, FileLocation targetFileLocation) {
    super(fileLocation);
    this.targetFileLocation = targetFileLocation;
  }
  
  /**
   * {@inheritDoc}
   */
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }

  /**
   * Gets the location of where to move the file.
   *
   * @return the location of where to move the file
   */
  public FileLocation getTargetFileLocation() {
    return targetFileLocation;
  }
}
