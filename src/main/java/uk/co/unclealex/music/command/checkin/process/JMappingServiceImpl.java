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
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JValidator;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.audio.JAudioMusicFileFactory;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFlacFileChecker;
import uk.co.unclealex.music.message.JMessageService;

/**
 * The default implementation of {@link JMappingService}.
 * 
 * @author alex
 * 
 */
public class JMappingServiceImpl implements JMappingService {

  /**
   * The {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to generate {@link uk.co.unclealex.music.JMusicFile}s from
   * FLAC files.
   */
  private final JAudioMusicFileFactory audioMusicFileFactory;

  /**
   * The {@link uk.co.unclealex.music.JValidator} used to validate that FLAC files are fully tagged.
   */
  private final JValidator validator;

  /**
   * The {@link uk.co.unclealex.music.files.JFlacFileChecker} used to see if a file is a FLAC file or not.
   */
  private final JFlacFileChecker flacFileChecker;

  /**
   * The message service used to display track information.
   */
  private final JMessageService messageService;

  /**
   * Instantiates a new mapping service impl.
   * 
   * @param audioMusicFileFactory
   *          the audio music file factory
   * @param validator
   *          the validator
   */
  @Inject
  public JMappingServiceImpl(
          JAudioMusicFileFactory audioMusicFileFactory,
          JValidator validator,
          JFlacFileChecker flacFileChecker,
          JMessageService messageService) {
    super();
    this.audioMusicFileFactory = audioMusicFileFactory;
    this.validator = validator;
    this.flacFileChecker = flacFileChecker;
    this.messageService = messageService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JActions mapPathsToMusicFiles(
      JActions actions,
      Iterable<? extends JFileLocation> fileLocations,
      Map<JFileLocation, JMusicFile> musicFilesByFileLocation) throws IOException {
    JAudioMusicFileFactory audioMusicFileFactory = getAudioMusicFileFactory();
    JFlacFileChecker flacFileChecker = getFlacFileChecker();
    JValidator validator = getValidator();
    JMessageService messageService = getMessageService();
    for (JFileLocation fileLocation : fileLocations) {
      Path path = fileLocation.resolve();
      if (flacFileChecker.isFlacFile(path)) {
        JMusicFile musicFile = audioMusicFileFactory.loadAndValidate(path);
        Set<ConstraintViolation<JMusicFile>> constraintViolations = validator.generateViolations(musicFile);
        if (constraintViolations.isEmpty()) {
          musicFilesByFileLocation.put(fileLocation, musicFile);
          messageService.printMessage(
              JMessageService.FOUND_TRACK,
              fileLocation,
              musicFile.getAlbumArtist(),
              musicFile.getAlbum(),
              musicFile.getDiscNumber(),
              musicFile.getTotalDiscs(),
              musicFile.getTrackNumber(),
              musicFile.getTotalTracks(),
              musicFile.getTitle());
        }
        else {
          for (ConstraintViolation<JMusicFile> constraintViolation : constraintViolations) {
            actions =
                actions.fail(
                    fileLocation,
                    String.format("%s: %s", fileLocation.resolve(), constraintViolation.getMessage()));
          }
        }
      }
      else {
        actions = actions.fail(fileLocation, JMessageService.NOT_FLAC);
      }
    }
    return actions;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to generate {@link uk.co.unclealex.music.JMusicFile}s
   * from FLAC files.
   * 
   * @return the {@link uk.co.unclealex.music.audio.JAudioMusicFileFactory} used to generate
   *         {@link uk.co.unclealex.music.JMusicFile}s from FLAC files
   */
  public JAudioMusicFileFactory getAudioMusicFileFactory() {
    return audioMusicFileFactory;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.JValidator} used to validate that FLAC files are fully
   * tagged.
   * 
   * @return the {@link uk.co.unclealex.music.JValidator} used to validate that FLAC files are fully
   *         tagged
   */
  public JValidator getValidator() {
    return validator;
  }

  public JFlacFileChecker getFlacFileChecker() {
    return flacFileChecker;
  }

  public JMessageService getMessageService() {
    return messageService;
  }

}
