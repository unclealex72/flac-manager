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

/**
 * A visitor that visits {@link JAction}s.
 * 
 * @author alex
 * 
 */
public interface JActionVisitor {

  /**
   * An implementation of {@link JActionVisitor} that throws an
   * {@link IllegalArgumentException} for unknown {@link JAction} types.
   * 
   * @author alex
   * 
   */
  public abstract class Default implements JActionVisitor {

    /**
     * Throw an {@link IllegalArgumentException}.
     * 
     * @param action
     *          The unknown action type being visited.
     */
    public void visit(JAction action) {
      throw new IllegalArgumentException(action.getClass() + " is not a valid action type.");
    }
  }

  /**
   * Throw an {@link IllegalArgumentException}.
   * 
   * @param action
   *          The unknown action type being visited.
   */
  public void visit(JAction action);

  /**
   * Move a file, making sure that any newly empty directories are removed.
   * 
   * @param moveAction
   *          The {@link JAction} containing the file information.
   */
  public void visit(JMoveAction moveAction) throws IOException;

  /**
   * Delete a file, making sure that any newly empty directories are removed.
   * 
   * @param moveAction
   *          The {@link JAction} containing the file information.
   */
  public void visit(JDeleteAction deleteAction) throws IOException;

  /**
   * Encode a file.
   * 
   * @param encodeAction
   *          The {@link JAction} containing all encoding information.
   */
  public void visit(JEncodeAction encodeAction) throws IOException;

  /**
   * Add artwork to a FLAC file.
   * 
   * @param addArtworkAction
   *          The {@link JAction} containing all the information needed to add
   *          artwork.
   */
  public void visit(JAddArtworkAction addArtworkAction) throws IOException;

  /**
   * Link a device file to an encoded file.
   * 
   * @param linkAction
   *          The {@link JAction} containing all the information needed to create
   *          the link.
   */
  public void visit(JLinkAction linkAction) throws IOException;

  /**
   * Unlink a device file from an encoded file.
   * 
   * @param unlinkAction
   *          The {@link JAction} containing all the information needed to remove
   *          the link.
   */
  public void visit(JUnlinkAction unlinkAction) throws IOException;

  /**
   * Indicate that a FLAC file could not be checked in or out.
   * 
   * @param failureAction
   *          The {@link JAction} containing information on why the FLAC file was
   *          invalid.
   */
  public void visit(JFailureAction failureAction) throws IOException;

  /**
   * Indicate that cover art needs to be downloaded for a file.
   * 
   * @param coverArtAction
   *          The {@link JAction} containing information on which FLAC file needs
   *          cover art and where it should be downloaded from.
   */
  public void visit(JCoverArtAction coverArtAction) throws IOException;

  /**
   * Add a change of ownership to the list of ownership changes.
   * 
   * @param changeOwnerAction
   *          The {@link JAction} containing information on which FLAC files need
   *          their ownerships changed.
   * @throws IOException
   */
  public void visit(JChangeOwnerAction changeOwnerAction) throws IOException;

  /**
   * Update the ownership of changed files in MusicBrainz.
   * 
   * @param updateOwnershipAction
   *          The {@link JAction} indicating changes are to be committed to
   *          MusicBrainz.
   * @throws IOException
   */
  public void visit(JUpdateOwnershipAction updateOwnershipAction) throws IOException;

}
