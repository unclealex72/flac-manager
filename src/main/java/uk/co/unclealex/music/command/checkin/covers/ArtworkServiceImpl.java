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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.files.FileLocation;

/**
 * The default implementation of {@link ArtworkService}.
 * @author alex
 *
 */
public class ArtworkServiceImpl extends CacheLoader<URI, CoverArt> implements ArtworkService {

  /**
   * The {@link AudioMusicFileFactory} used to create a {@link MusicFile} from a {@link FileLocation}.
   */
  private final AudioMusicFileFactory audioMusicFileFactory;
  
  /**
   * The cache used to store previously downloaded cover art.
   */
  private final LoadingCache<URI, CoverArt> cache;
  
  @Inject
  public ArtworkServiceImpl(AudioMusicFileFactory audioMusicFileFactory) {
    super();
    this.audioMusicFileFactory = audioMusicFileFactory;
    cache = CacheBuilder.newBuilder().maximumSize(100).build(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CoverArt load(URI coverArtUri) throws Exception {
    return downloadCoverArt(coverArtUri);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void addArwork(Path path, URI coverArtUri) throws IOException {
    CoverArt coverArt;
    try {
      coverArt = getCache().get(coverArtUri);
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
    MusicFile musicFile = getAudioMusicFileFactory().loadAndValidate(path);
    musicFile.setCoverArt(coverArt);
    musicFile.commit();
  }

  protected CoverArt downloadCoverArt(URI coverArtUri) throws IOException, MalformedURLException {
    CoverArt coverArt;
    String contentType;
    ByteArrayOutputStream out;
    Path coverArtFile = Files.createTempFile("cover-art-", ".art");
    try {
      try (InputStream in = coverArtUri.toURL().openStream()) {
        Files.copy(in, coverArtFile, StandardCopyOption.REPLACE_EXISTING);
      }
      contentType = Files.probeContentType(coverArtFile);
      out = new ByteArrayOutputStream();
      Files.copy(coverArtFile, out);
    }
    finally {
      Files.delete(coverArtFile);
    }
    coverArt = new CoverArt(out.toByteArray(), contentType);
    return coverArt;
  }

  public AudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

  public LoadingCache<URI, CoverArt> getCache() {
    return cache;
  }
}
