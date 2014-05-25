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
import javax.inject.Provider;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionFunction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JCoverArtAction;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A {@link JFlacFilesValidator} looks for {@link uk.co.unclealex.music.JCoverArt} actions. If cover art
 * is missing then it will look for it and, if found, prepend
 * {@link uk.co.unclealex.music.action.JAddArtworkAction}s. If no artwork is found then failures are added.
 * Calls to {@link ArtworkSearchingService} are not cached.
 * 
 * @author alex
 * 
 */
public class JFindMissingCoverArtFlacFilesValidator implements JFlacFilesValidator {

  /**
   * A {@link Provider} for {@link uk.co.unclealex.music.action.JActions} that then allows {@link uk.co.unclealex.music.action.JAction}s to
   * be prepended.
   */
  private final Provider<JActions> actionProvider;

  /**
   * The {@link ArtworkSearchingService} used to look for missing artwork.
   */
  private final ArtworkSearchingService artworkSearchingService;

  /**
   * Instantiates a new find missing cover art flac file validator.
   * 
   * @param actionProvider
   *          the action supplier
   * @param artworkSearchingService
   *          the artwork searching service
   */
  @Inject
  public JFindMissingCoverArtFlacFilesValidator(
          Provider<JActions> actionProvider,
          ArtworkSearchingService artworkSearchingService) {
    super();
    this.actionProvider = actionProvider;
    this.artworkSearchingService = artworkSearchingService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) throws IOException {
    Function<JAction, JFileLocation> coverArtFunction = new JActionFunction<JFileLocation>(null) {
      @Override
      protected JFileLocation visitAndReturn(JCoverArtAction coverArtAction) {
        return coverArtAction.getFileLocation();
      }
    };
    SortedSet<JFileLocation> fileLocationsRequiringCoverArt =
        Sets.newTreeSet(Iterables.filter(Lists.transform(actions.get(), coverArtFunction), Predicates.notNull()));
    ArtworkSearchingService artworkSearchingService = getArtworkSearchingService();
    for (JFileLocation fileLocation : fileLocationsRequiringCoverArt) {
      URI coverArtUri = artworkSearchingService.findArtwork(musicFilesByFlacPath.get(fileLocation));
      if (coverArtUri == null) {
        actions = actions.fail(fileLocation, JMessageService.MISSING_ARTWORK);
      }
      else {
        actions = getActionProvider().get().addArtwork(fileLocation, coverArtUri).then(actions);
      }
    }
    return actions;
  }

  /**
   * Gets the a {@link Provider} for {@link uk.co.unclealex.music.action.JActions} that then allows.
   * 
   * @return the a {@link Provider} for {@link uk.co.unclealex.music.action.JActions} that then allows
   *         {@link uk.co.unclealex.music.action.JAction}s to be prepended. {@link uk.co.unclealex.music.action.JAction}s to be prepended
   */
  public Provider<JActions> getActionProvider() {
    return actionProvider;
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
