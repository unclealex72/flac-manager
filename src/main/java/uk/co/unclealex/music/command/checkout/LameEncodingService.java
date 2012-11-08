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

package uk.co.unclealex.music.command.checkout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.inject.Inject;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileService;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.process.BuildableProcessRequest;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

/**
 * An instance of {@link EncodingService} that uses LAME.
 * 
 * @author alex
 */
@PackagesRequired("lame")
public class LameEncodingService implements EncodingService {

  /**
   * The {@link ProcessRequestBuilder} used to build the lame process.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  /**
   * The {@link MusicFileService} used for copying tagging information.
   */
  private final MusicFileService musicFileService;

  /**
   * The {@link AudioMusicFileFactory} used to create {@link MusicFile}s from paths.
   */
  private final AudioMusicFileFactory audioMusicFileFactory;
  
  @Inject
  public LameEncodingService(
      ProcessRequestBuilder processRequestBuilder,
      MusicFileService musicFileService,
      AudioMusicFileFactory audioMusicFileFactory) {
    super();
    this.processRequestBuilder = processRequestBuilder;
    this.musicFileService = musicFileService;
    this.audioMusicFileFactory = audioMusicFileFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void encode(FileLocation flacFileLocation, MusicFile flacMusicFile, FileLocation encodedFileLocation)
      throws IOException {
    Path flacFile = flacFileLocation.resolve();
    Path encodedFile = encodedFileLocation.resolve();
    Path temporaryFile = encodedFile.resolveSibling(UUID.randomUUID().toString() + ".mp3.part");
    try {
      BuildableProcessRequest processRequest =
          getProcessRequestBuilder().forResource("flac2mp3").withArguments(
              flacFile.toAbsolutePath().toString(),
              temporaryFile.toAbsolutePath().toString());
      processRequest.execute();
      MusicFile mp3MusicFile = getAudioMusicFileFactory().load(temporaryFile);
      getMusicFileService().transfer(flacMusicFile, mp3MusicFile);
      Files.move(temporaryFile, encodedFile, StandardCopyOption.ATOMIC_MOVE);
    }
    finally {
      Files.deleteIfExists(temporaryFile);
    }
  }

  /**
   * Gets the {@link ProcessRequestBuilder} used to build the lame process.
   * 
   * @return the {@link ProcessRequestBuilder} used to build the lame process
   */
  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }

  /**
   * Gets the {@link MusicFileService} used for copying tagging information.
   * 
   * @return the {@link MusicFileService} used for copying tagging information
   */
  public MusicFileService getMusicFileService() {
    return musicFileService;
  }

  public AudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

}
