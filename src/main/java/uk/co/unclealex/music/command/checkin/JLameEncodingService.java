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
import java.nio.file.Path;

import javax.inject.Inject;

import uk.co.unclealex.music.JMusicFileService;
import uk.co.unclealex.music.audio.JAudioMusicFileFactory;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.files.JFileUtils;
import uk.co.unclealex.process.BuildableProcessRequest;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

/**
 * An instance of {@link JEncodingService} that uses LAME.
 * 
 * @author alex
 */
@PackagesRequired("lame")
public class JLameEncodingService extends JAbstractEncodingService implements JEncodingService {

  /**
   * The {@link ProcessRequestBuilder} used to build the lame process.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  /**
   * Instantiates a new lame encoding service.
   * 
   * @param processRequestBuilder
   *          the process request builder
   * @param musicFileService
   *          the music file service
   * @param audioMusicFileFactory
   *          the audio music file factory
   * @param fileUtils
   *          the file utils
   * @param fileLocationFactory
   *          the file location factory
   */
  @Inject
  public JLameEncodingService(
          ProcessRequestBuilder processRequestBuilder,
          JMusicFileService musicFileService,
          JAudioMusicFileFactory audioMusicFileFactory,
          JFileUtils fileUtils,
          JFileLocationFactory fileLocationFactory) {
    super(musicFileService, audioMusicFileFactory, fileUtils, fileLocationFactory);
    this.processRequestBuilder = processRequestBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void encode(Path flacFile, Path encodedFile) throws IOException {
    BuildableProcessRequest processRequest =
        getProcessRequestBuilder().forResource("flac2mp3").withArguments(
            flacFile.toAbsolutePath().toString(),
            encodedFile.toAbsolutePath().toString());
    processRequest.executeAndWait();
  }

  /**
   * Gets the {@link ProcessRequestBuilder} used to build the lame process.
   * 
   * @return the {@link ProcessRequestBuilder} used to build the lame process
   */
  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }

}
