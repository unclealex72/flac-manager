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

package uk.co.unclealex.music.message;

/**
 * An interface for classes that can print internationalised messages to users.
 * 
 * @author alex
 * 
 */
public interface MessageService {

  /**
   * The key for producing an artwork adding message.
   */
  public String ARTWORK = "artwork";

  /**
   * The key for producing an encoding message.
   */
  public String ENCODE = "encode";

  /**
   * The key for producing a delete message.
   */
  public String DELETE = "delete";

  /**
   * The key for producing a move message.
   */
  public String MOVE = "move";

  /**
   * The key for producing a not flac file message.
   */
  public String NOT_FLAC = "notFlac";

  /**
   * The key for producing a missing artwork message.
   */
  public String MISSING_ARTWORK = "missingArtwork";

  /**
   * The key for producing an overwrite message.
   */
  public String OVERWRITE = "overwrite";

  /**
   * The key for producing non unique messages.
   */
  public String NON_UNIQUE = "nonUnique";

  /**
   * The key for producing not owned messages.
   */
  public String NOT_OWNED = "notOwned";

  /**
   * The key for producing not owned messages.
   */
  public String NO_OWNER_INFORMATION = "noOwner";

  /**
   * The key for producing link messages.
   */
  public String LINK = "link";

  /**
   * The key for producing link messages.
   */
  public String UNLINK = "unlink";

  /**
   * The key for producing unknown user messages.
   */
  public String UNKNOWN_USER = "unknownUser";

  /**
   * The key for producing add owner messages.
   */
  public String ADD_OWNER = "addOwner";

  /**
   * The key for producing remove owner messages.
   */
  public String REMOVE_OWNER = "removeOwner";

  /**
   * The key for producing commit ownership changes messages.
   */
  public String COMMIT_OWNERSHIP = "commitOwnership";

  /**
   * Print an internationalised message.
   * 
   * @param template
   *          The template key used to select the message.
   * @param parameters
   *          A list of parameters required by the message template.
   */
  public void printMessage(String template, Object... parameters);
}
