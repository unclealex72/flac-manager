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
 * An action used to write-unprotect a file.
 * 
 * @author alex
 * 
 */
public class UnprotectAction extends AbstractAction implements Action {

  /**
   * Instantiates a new unprotect action.
   *
   * @param location the location
   */
  public UnprotectAction(FileLocation fileLocation) {
    super(fileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }
}