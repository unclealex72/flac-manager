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
 * A visitor that visits {@link Action}s.
 * 
 * @author alex
 * 
 */
public interface ActionVisitor {

  /**
   * An implementation of {@link ActionVisitor} that throws an
   * {@link IllegalArgumentException} for unknown {@link Action} types.
   * 
   * @author alex
   * 
   */
  public abstract class Default implements ActionVisitor {

    /**
     * Throw an {@link IllegalArgumentException}.
     * 
     * @param action
     *          The unknown action type being visited.
     */
    public void visit(Action action) {
      throw new IllegalArgumentException(action.getClass() + " is not a valid action type.");
    }
  }

  /**
   * Throw an {@link IllegalArgumentException}.
   * 
   * @param action
   *          The unknown action type being visited.
   */
  public void visit(Action action);

  /**
   * Write protect a file.
   * 
   * @param protectAction
   *          The {@link Action} containing the file information.
   */
  public void visit(ProtectAction protectAction) throws IOException;

  /**
   * Write unprotect a file.
   * 
   * @param unprotectAction
   *          The {@link Action} containing the file information.
   * @throws IOException 
   */
  public void visit(UnprotectAction unprotectAction) throws IOException;

  /**
   * Move a file, making sure that any newly empty directories are removed.
   * 
   * @param moveAction
   *          The {@link Action} containing the file information.
   */
  public void visit(MoveAction moveAction) throws IOException;

  /**
   * Delete a file, making sure that any newly empty directories are removed.
   * 
   * @param moveAction
   *          The {@link Action} containing the file information.
   */
  public void visit(DeleteAction deleteAction) throws IOException;

  /**
   * Encode a file.
   * 
   * @param encodeAction
   *          The {@link Action} containing all encoding information.
   */
  public void visit(EncodeAction encodeAction) throws IOException;

  /**
   * Add artwork to a FLAC file.
   * 
   * @param addArtworkAction
   *          The {@link Action} containing all the information needed to add
   *          artwork.
   */
  public void visit(AddArtworkAction addArtworkAction) throws IOException;

  /**
   * Indicate that a FLAC file could not be checked in or out.
   * 
   * @param failureAction
   *          The {@link Action} containing information on why the FLAC file was
   *          invalid.
   */
  public void visit(FailureAction failureAction) throws IOException;

  /**
   * Indicate that cover art needs to be downloaded for a file.
   * 
   * @param coverArtAction
   *          The {@link Action} containing information on which FLAC file needs
   *          cover art and where it should be downloaded from.
   */
  public void visit(CoverArtAction coverArtAction) throws IOException;

}
