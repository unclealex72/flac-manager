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

import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.files.FileLocation;

/**
 * An action used to delete a symbolic link from a device repository to the encoded repository.
 * 
 * @author alex
 * 
 */
public class UnlinkAction extends AbstractAction implements Action {

  /**
   * The location of the MP3 file to be encoded.
   */
  private final FileLocation linkLocation;

  /**
   * The old owner of the file.
   */
  private final User owner;
  
  /**
   * Instantiates a new link action.
   *
   * @param fileLocation the file location
   * @param owner the owner
   * @param linkLocation the link location
   */
  public UnlinkAction(FileLocation fileLocation, User owner, FileLocation linkLocation) {
    super(fileLocation);
    this.linkLocation = linkLocation;
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }

  /**
   * Gets the location of the MP3 file to be encoded.
   *
   * @return the location of the MP3 file to be encoded
   */
  public FileLocation getLinkLocation() {
    return linkLocation;
  }

  /**
   * Gets the old owner of the file.
   *
   * @return the old owner of the file
   */
  public User getOwner() {
    return owner;
  }
}
