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

package uk.co.unclealex.music.musicbrainz;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;

import uk.co.unclealex.music.configuration.User;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;

/**
 * The Class MusicBrainzClientImpl.
 *
 * @author alex
 */
public class MusicBrainzClientImpl implements MusicBrainzClient {

  /**
   * The maximum number of release IDs allowed in a PUT or DELETE request.
   */
  private static final int RELEASE_PATH_LIMIT = 400;

  /** 
   * The maximum size of a query.
   */
  private static final int LIMIT = 100;

  /** 
   * The name of the user's collection. 
   */
  private static final String ALL_MY_MUSIC = "All my music";

  /**
   * The namespace for XML documents returned by MusicBrainz.
   */
  private static Namespace MUSICBRAINZ_NS = Namespace.getNamespace("http://musicbrainz.org/ns/mmd-2.0#");

  /**
   * The {@link MusicBrainzWebResourceFactory} used to talk to MusicBrainz with
   * Jersey.
   */
  private final MusicBrainzWebResourceFactory musicBrainzWebResourceFactory;

  /**
   * Instantiates a new music brainz client impl.
   *
   * @param musicBrainzWebResourceFactory the music brainz web resource factory
   */
  @Inject
  public MusicBrainzClientImpl(MusicBrainzWebResourceFactory musicBrainzWebResourceFactory) {
    super();
    this.musicBrainzWebResourceFactory = musicBrainzWebResourceFactory;
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public List<String> getRelasesForOwner(User user) throws NoCollectionException {
    ImmutablePair<String, Integer> collectionInfo = findUsersCollection(user);
    String collectionId = collectionInfo.getLeft();
    int collectionSize = collectionInfo.getRight();
    List<String> releases = Lists.newArrayListWithCapacity(collectionSize);
    for (int offset = 0; offset < collectionSize; offset += LIMIT) {
      Document document = releases(user, collectionId)
          .queryParam("offset", Integer.toString(offset))
          .queryParam("limit", Integer.toString(LIMIT)).get(Document.class);
      for (Element el : document.getDescendants(new ElementFilter("release", MUSICBRAINZ_NS))) {
        String releaseId = el.getAttributeValue("id");
        releases.add(releaseId);
      }
    }
    return releases;
  }

  /**
   * Get the web resource for a users collection.
   * @param user The user who is making the request.
   * @param collectionId The id of the collection to retrieve or alter.
   * @return A web resource for a user's collection.
   */
  protected WebResource releases(User user, String collectionId) {
    return getMusicBrainzWebResourceFactory()
        .webResource(user)
        .path("collection")
        .path(collectionId)
        .path("releases");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addReleases(User user, Iterable<String> newReleaseIds) throws NoCollectionException {
    WebResourceAction put = new WebResourceAction() {
      @Override
      public void act(WebResource webResource) {
        webResource.put();
      }
    };
    alterReleases(user, newReleaseIds, put);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeReleases(User user, Iterable<String> oldReleaseIds) throws NoCollectionException {
    WebResourceAction delete = new WebResourceAction() {
      @Override
      public void act(WebResource webResource) {
        webResource.put();
      }
    };
    alterReleases(user, oldReleaseIds, delete);
  }
  
  /**
   * Alter a user's collection of releases.
   * @param user The user whose collection is being searched.
   * @param releaseIds The ids of the affected releases.
   * @param action The web method to execute.
   * @throws NoCollectionException 
   */
  private void alterReleases(User user, Iterable<String> releaseIds, WebResourceAction action) throws NoCollectionException {
    String collectionId = findUsersCollection(user).getLeft();
    for (Iterable<String> partition : Iterables.partition(releaseIds, RELEASE_PATH_LIMIT)) {
      String releasesPath = Joiner.on(';').join(partition);
      action.act(releases(user, collectionId).path(releasesPath));
    }
  }

  /**
   * An interface to encapsulate a method being called on a web resource.
   * @author alex
   *
   */
  interface WebResourceAction {
    
    public void act(WebResource webResource);
  }
  /**
   * Find a user's unique collection.
   * 
   * @param user
   *          The user whose collection is being searched for.
   * @return An {@link ImmutablePair} containing the ID of the collection and
   *         the number of entries it requires.
   * @throws NoCollectionException
   *           thrown if a unique collection cannot be found.
   */
  public ImmutablePair<String, Integer> findUsersCollection(User user) throws NoCollectionException {
    Document doc = getMusicBrainzWebResourceFactory().webResource(user).path("collection").get(Document.class);
    List<Element> collectionElements =
        Lists.newArrayList((Iterable<Element>) doc.getDescendants(new ElementFilter("collection", MUSICBRAINZ_NS)));
    int size = collectionElements.size();
    Element collectionElement;
    if (size == 0) {
      throw new NoCollectionException("No collections found.");
    }
    else if (size == 1) {
      collectionElement = collectionElements.get(0);
    }
    else {
      Predicate<Element> isAllMyMusicPredicate = new Predicate<Element>() {
        public boolean apply(Element element) {
          Element nameElement = element.getChild("name", MUSICBRAINZ_NS);
          return nameElement != null && ALL_MY_MUSIC.equals(nameElement.getTextNormalize());
        }
      };
      collectionElement = Iterables.find(collectionElements, isAllMyMusicPredicate, null);
      if (collectionElement == null) {
        throw new NoCollectionException("Cannot find a unique collection.");
      }
    }
    String releaseId = collectionElement.getAttributeValue("id");
    int releaseCount =
        Integer.parseInt(collectionElement.getChild("release-list", MUSICBRAINZ_NS).getAttributeValue("count"));
    return new ImmutablePair<String, Integer>(releaseId, releaseCount);
  }

  /**
   * Gets the {@link MusicBrainzWebResourceFactory} used to talk to MusicBrainz with Jersey.
   *
   * @return the {@link MusicBrainzWebResourceFactory} used to talk to MusicBrainz with Jersey
   */
  public MusicBrainzWebResourceFactory getMusicBrainzWebResourceFactory() {
    return musicBrainzWebResourceFactory;
  }

}
