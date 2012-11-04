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

package uk.co.unclealex.music.checkin.process;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedMap;

import javax.inject.Inject;

import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.audio.AudioMusicFileFactory;

import com.google.common.collect.Maps;

/**
 * The default implementation of {@link MappingService}.
 * @author alex
 *
 */
public class MappingServiceImpl implements MappingService {

  /**
   * The {@link AudioMusicFileFactory} used to generate {@link MusicFile}s from FLAC files.
   */
  private final AudioMusicFileFactory audioMusicFileFactory;
  
  /**
   * Instantiates a new mapping service impl.
   *
   * @param audioMusicFileFactory the audio music file factory
   */
  @Inject
  public MappingServiceImpl(AudioMusicFileFactory audioMusicFileFactory) {
    super();
    this.audioMusicFileFactory = audioMusicFileFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedMap<Path, MusicFile> mapPathsToMusicFiles(Iterable<Path> paths) throws IOException {
    SortedMap<Path, MusicFile> map = Maps.newTreeMap();
    AudioMusicFileFactory audioMusicFileFactory = getAudioMusicFileFactory();
    for (Path path : paths) {
      MusicFile musicFile = audioMusicFileFactory.load(path);
      map.put(path, musicFile);
    }
    return map;
  }

  /**
   * Gets the {@link AudioMusicFileFactory} used to generate {@link MusicFile}s from FLAC files.
   *
   * @return the {@link AudioMusicFileFactory} used to generate {@link MusicFile}s from FLAC files
   */
  public AudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

}
