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

package uk.co.unclealex.music.action;

import java.io.IOException;

import uk.co.unclealex.music.common.DataObject;
import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.files.FileLocation;

/**
 * An action used to encode a FLAC file into an MP3 file.
 * 
 * @author alex
 * 
 */
public class EncodeAction extends DataObject implements Action {

  /**
   * The location of the FLAC file to encode.
   */
  private final FileLocation flacFileLocation;
  
  /**
   * The location of the MP3 file to be encoded.
   */
  private final FileLocation encodedFileLocation;
  
  /**
   * The {@link MusicFile} used to tag the MP3 file.
   */
  private final MusicFile flacMusicFile;
  
  /**
   * Instantiates a new encode action.
   *
   * @param flacFileLocation the flac file location
   * @param encodedFileLocation the encoded file location
   * @param flacMusicFile the flac music file
   */
  public EncodeAction(FileLocation flacFileLocation, FileLocation encodedFileLocation, MusicFile flacMusicFile) {
    super();
    this.flacFileLocation = flacFileLocation;
    this.encodedFileLocation = encodedFileLocation;
    this.flacMusicFile = flacMusicFile;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }


  /**
   * Gets the location of the FLAC file to encode.
   *
   * @return the location of the FLAC file to encode
   */
  public FileLocation getFlacFileLocation() {
    return flacFileLocation;
  }


  /**
   * Gets the location of the MP3 file to be encoded.
   *
   * @return the location of the MP3 file to be encoded
   */
  public FileLocation getEncodedFileLocation() {
    return encodedFileLocation;
  }


  /**
   * Gets the {@link MusicFile} used to tag the MP3 file.
   *
   * @return the {@link MusicFile} used to tag the MP3 file
   */
  public MusicFile getFlacMusicFile() {
    return flacMusicFile;
  }
}
