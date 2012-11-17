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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileService;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.files.FileUtils;

/**
 * @author alex
 * 
 */
public abstract class AbstractEncodingService implements EncodingService {

  /**
   * The {@link FileUtils} used to move and write protect files.
   */
  private final FileUtils fileUtils;

  /**
   * The {@link FileLocationFactory} used to create {@link FileLocation}s.
   */
  private final FileLocationFactory fileLocationFactory;
  
  /**
   * The {@link MusicFileService} used for copying tagging information.
   */
  private final MusicFileService musicFileService;

  /**
   * The {@link AudioMusicFileFactory} used to create {@link MusicFile}s from
   * paths.
   */
  private final AudioMusicFileFactory audioMusicFileFactory;

  public AbstractEncodingService(
      MusicFileService musicFileService,
      AudioMusicFileFactory audioMusicFileFactory,
      FileUtils fileUtils,
      FileLocationFactory fileLocationFactory) {
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
  public void encode(FileLocation flacFileLocation, MusicFile flacMusicFile, FileLocation encodedFileLocation)
      throws IOException {
    Path flacFile = flacFileLocation.resolve();
    FileLocation temporaryFileLocation =
        getFileLocationFactory().createStagingFileLocation(Paths.get(UUID.randomUUID().toString() + ".mp3"));
    Path temporaryPath = temporaryFileLocation.resolve();
    try {
      encode(flacFile, temporaryPath);
      MusicFile mp3MusicFile = getAudioMusicFileFactory().load(temporaryPath);
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
   * Gets the {@link MusicFileService} used for copying tagging information.
   * 
   * @return the {@link MusicFileService} used for copying tagging information
   */
  public MusicFileService getMusicFileService() {
    return musicFileService;
  }

  /**
   * Gets the {@link AudioMusicFileFactory} used to create {@link MusicFile}s
   * from paths.
   * 
   * @return the {@link AudioMusicFileFactory} used to create {@link MusicFile}s
   *         from paths
   */
  public AudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

  /**
   * Gets the {@link FileUtils} used to move and write protect files.
   * 
   * @return the {@link FileUtils} used to move and write protect files
   */
  public FileUtils getFileUtils() {
    return fileUtils;
  }

  /**
   * Gets the {@link FileLocationFactory} used to create {@link FileLocation}s.
   * 
   * @return the {@link FileLocationFactory} used to create {@link FileLocation}
   *         s
   */
  public FileLocationFactory getFileLocationFactory() {
    return fileLocationFactory;
  }

}