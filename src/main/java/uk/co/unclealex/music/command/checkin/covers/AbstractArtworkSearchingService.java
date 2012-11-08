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

package uk.co.unclealex.music.command.checkin.covers;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import uk.co.unclealex.music.DataObject;
import uk.co.unclealex.music.MusicFile;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A base class for {@link ArtworkSearchingService}s that allow for null-safe
 * checking of a property of a {@link MusicFile}. This class also caches any
 * results.
 * 
 * @author alex
 * 
 */
public abstract class AbstractArtworkSearchingService implements
    ArtworkSearchingService {

  /**
   * The cache to use for caching previous queries to the artwork service.
   */
  private final LoadingCache<String, UriHolder> cache;

  /**
   * Instantiates a new abstract artwork searching service.
   */
  public AbstractArtworkSearchingService() {
    CacheLoader<String, UriHolder> cacheLoader = new CacheLoader<String, UriHolder>() {
      public UriHolder load(String albumIdentifier) throws Exception {
        return new UriHolder(loadArtwork(albumIdentifier));        
      }
    };
    this.cache = CacheBuilder.newBuilder().maximumSize(200).build(cacheLoader);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public URI findArtwork(MusicFile musicFile) throws IOException {
    String albumIdentifier = locateAlbumIdentifier(musicFile);
    if (albumIdentifier == null) {
      return null;
    }
    try {
      return getCache().get(albumIdentifier).getUri();
    }
    catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      else {
        throw new IOException(e);
      }
    }
  }

  /**
   * Find an album's artwork given an album identifier.
   *
   * @param albumIdentifier The unique string used to find the album. This will not be null.
   * @return The URI for the album artwork or null if none could be found.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected abstract URI loadArtwork(String albumIdentifier) throws IOException;

  /**
   * Locate an album identifier from a {@link MusicFile}.
   * 
   * @param musicFile
   *          The {@link MusicFile} being searched.
   * @return The album identifier or null if no identifier could be found and
   *         thus no artwork should be searched for.
   */
  protected abstract String locateAlbumIdentifier(MusicFile musicFile);

  /**
   * A convenience class to allow null values to be returned and held in the cache.
   * @author alex
   *
   */
  class UriHolder extends DataObject {
    
    /**
     * The URI held within this object.
     */
    private final URI uri;

    /**
     * Instantiates a new uri holder.
     *
     * @param uri the uri
     */
    public UriHolder(URI uri) {
      super();
      this.uri = uri;
    }
    
    /**
     * Gets the URI held within this object.
     *
     * @return the URI held within this object
     */
    public URI getUri() {
      return uri;
    }
  }
  /**
   * Gets the cache to use for caching previous queries to the artwork service.
   *
   * @return the cache to use for caching previous queries to the artwork service
   */
  public LoadingCache<String, UriHolder> getCache() {
    return cache;
  }
}
