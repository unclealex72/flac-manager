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

package uk.co.unclealex.music.command.checkin.process;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedMap;

import javax.inject.Inject;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FlacFileChecker;

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
   * The {@link Validator} used to validate that FLAC files are fully tagged.
   */
  private final Validator validator;

  private final FlacFileChecker flacFileChecker;
  
  /**
   * Instantiates a new mapping service impl.
   *
   * @param audioMusicFileFactory the audio music file factory
   * @param validator the validator
   */
  @Inject
  public MappingServiceImpl(AudioMusicFileFactory audioMusicFileFactory, Validator validator) {
    super();
    this.audioMusicFileFactory = audioMusicFileFactory;
    this.validator = validator;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Actions mapPathsToMusicFiles(
      Actions actions,
      Iterable<FileLocation> fileLocations,
      SortedMap<FileLocation, MusicFile> musicFilesByFileLocation) throws IOException {
    AudioMusicFileFactory audioMusicFileFactory = getAudioMusicFileFactory();
    Validator validator = getValidator();
    for (FileLocation fileLocation : fileLocations) {
      Path path = fileLocation.resolve();
      
      MusicFile musicFile = audioMusicFileFactory.load(path);
      musicFilesByFileLocation.put(fileLocation, musicFile);
    }
    return actions;
  }

  /**
   * Gets the {@link AudioMusicFileFactory} used to generate {@link MusicFile}s from FLAC files.
   *
   * @return the {@link AudioMusicFileFactory} used to generate {@link MusicFile}s from FLAC files
   */
  public AudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

  /**
   * Gets the {@link Validator} used to validate that FLAC files are fully tagged.
   *
   * @return the {@link Validator} used to validate that FLAC files are fully tagged
   */
  public Validator getValidator() {
    return validator;
  }

}
