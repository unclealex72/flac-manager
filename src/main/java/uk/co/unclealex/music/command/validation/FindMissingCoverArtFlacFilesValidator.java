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

package uk.co.unclealex.music.command.validation;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionFunction;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.AddArtworkAction;
import uk.co.unclealex.music.action.CoverArtAction;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.message.MessageService;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A {@link FlacFilesValidator} looks for {@link CoverArt} actions. If cover art
 * is missing then it will look for it and, if found, prepend
 * {@link AddArtworkAction}s. If no artwork is found then failures are added.
 * Calls to {@link ArtworkSearchingService} are not cached.
 * 
 * @author alex
 * 
 */
public class FindMissingCoverArtFlacFilesValidator implements FlacFilesValidator {

  /**
   * A {@link Supplier} for {@link Actions} that then allows {@link Action}s to
   * be prepended.
   */
  private final Supplier<Actions> actionSupplier;

  /**
   * The {@link ArtworkSearchingService} used to look for missing artwork.
   */
  private final ArtworkSearchingService artworkSearchingService;

  /**
   * Instantiates a new find missing cover art flac file validator.
   * 
   * @param actionSupplier
   *          the action supplier
   * @param artworkSearchingService
   *          the artwork searching service
   */
  @Inject
  public FindMissingCoverArtFlacFilesValidator(
      Supplier<Actions> actionSupplier,
      ArtworkSearchingService artworkSearchingService) {
    super();
    this.actionSupplier = actionSupplier;
    this.artworkSearchingService = artworkSearchingService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) throws IOException {
    Function<Action, FileLocation> coverArtFunction = new ActionFunction<FileLocation>(null) {
      @Override
      protected FileLocation visitAndReturn(CoverArtAction coverArtAction) {
        return coverArtAction.getFileLocation();
      }
    };
    SortedSet<FileLocation> fileLocationsRequiringCoverArt =
        Sets.newTreeSet(Iterables.filter(Lists.transform(actions.get(), coverArtFunction), Predicates.notNull()));
    ArtworkSearchingService artworkSearchingService = getArtworkSearchingService();
    for (FileLocation fileLocation : fileLocationsRequiringCoverArt) {
      URI coverArtUri = artworkSearchingService.findArtwork(musicFilesByFlacPath.get(fileLocation));
      if (coverArtUri == null) {
        actions = actions.fail(fileLocation, MessageService.MISSING_ARTWORK);
      }
      else {
        actions = getActionSupplier().get().addArtwork(fileLocation, coverArtUri).then(actions);
      }
    }
    return actions;
  }

  /**
   * Gets the a {@link Supplier} for {@link Actions} that then allows.
   * 
   * @return the a {@link Supplier} for {@link Actions} that then allows
   *         {@link Action}s to be prepended. {@link Action}s to be prepended
   */
  public Supplier<Actions> getActionSupplier() {
    return actionSupplier;
  }

  /**
   * Gets the {@link ArtworkSearchingService} used to look for missing artwork.
   * 
   * @return the {@link ArtworkSearchingService} used to look for missing
   *         artwork
   */
  public ArtworkSearchingService getArtworkSearchingService() {
    return artworkSearchingService;
  }

}
