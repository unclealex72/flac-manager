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

package uk.co.unclealex.music.command.checkin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileService;
import uk.co.unclealex.music.audio.JAudioMusicFileFactory;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.files.JFileUtils;

/**
 * @author alex
 * 
 */
public abstract class JAbstractEncodingService implements JEncodingService {

  /**
   * The {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files.
   */
  private final JFileUtils fileUtils;

  /**
   * The {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}s.
   */
  private final JFileLocationFactory fileLocationFactory;
  
  /**
   * The {@link uk.co.unclealex.music.JMusicFileService} used for copying tagging information.
   */
  private final JMusicFileService musicFileService;

  /**
   * The {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to create {@link uk.co.unclealex.music.JMusicFile}s from
   * paths.
   */
  private final JAudioMusicFileFactory audioMusicFileFactory;

  public JAbstractEncodingService(
          JMusicFileService musicFileService,
          JAudioMusicFileFactory audioMusicFileFactory,
          JFileUtils fileUtils,
          JFileLocationFactory fileLocationFactory) {
    super();
    this.musicFileService = musicFileService;
    this.audioMusicFileFactory = audioMusicFileFactory;
    this.fileUtils = fileUtils;
    this.fileLocationFactory = fileLocationFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void encode(JFileLocation flacFileLocation, JMusicFile flacMusicFile, JFileLocation encodedFileLocation)
      throws IOException {
    Path flacFile = flacFileLocation.resolve();
    JFileLocation temporaryFileLocation =
        getFileLocationFactory().createStagingFileLocation(Paths.get(UUID.randomUUID().toString() + ".mp3"));
    Path temporaryPath = temporaryFileLocation.resolve();
    try {
      encode(flacFile, temporaryPath);
      JMusicFile mp3MusicFile = getAudioMusicFileFactory().load(temporaryPath);
      getMusicFileService().transfer(flacMusicFile, mp3MusicFile);
      getFileUtils().move(temporaryFileLocation, encodedFileLocation);
    }
    finally {
      Files.deleteIfExists(temporaryPath);
    }
  }

  /**
   * Actually encode the FLAC file.
   * @param flacFile The FLAC file to encode.
   * @param encodedFile The location of where to encode to.
   * @throws IOException
   */
  protected abstract void encode(Path flacFile, Path encodedFile) throws IOException;

  /**
   * Gets the {@link uk.co.unclealex.music.JMusicFileService} used for copying tagging information.
   * 
   * @return the {@link uk.co.unclealex.music.JMusicFileService} used for copying tagging information
   */
  public JMusicFileService getMusicFileService() {
    return musicFileService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to create {@link uk.co.unclealex.music.JMusicFile}s
   * from paths.
   * 
   * @return the {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to create {@link uk.co.unclealex.music.JMusicFile}s
   *         from paths
   */
  public JAudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files
   */
  public JFileUtils getFileUtils() {
    return fileUtils;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}s.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileLocationFactory} used to create {@link uk.co.unclealex.music.files.JFileLocation}
   *         s
   */
  public JFileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

}