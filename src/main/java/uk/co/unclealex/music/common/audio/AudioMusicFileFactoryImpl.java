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

package uk.co.unclealex.music.common.audio;

import java.io.IOException;
import java.nio.file.Path;

import javax.validation.ConstraintViolationException;

import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.Validator;

/**
 * The default implementation of {@link AudioMusicFileFactory}.
 * @author alex
 *
 */
public class AudioMusicFileFactoryImpl implements AudioMusicFileFactory {

  /**
   * The {@link Validator} used to validate that audio files have all the required tags.
   */
  private final Validator validator;

  
  /**
   * Instantiates a new audio music file factory.
   *
   * @param validator the validator
   */
  public AudioMusicFileFactoryImpl(Validator validator) {
    super();
    this.validator = validator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MusicFile load(Path musicFilePath) throws IOException, ConstraintViolationException {
    MusicFile musicFile = new AudioMusicFile(musicFilePath);
    return getValidator().validate(musicFile, "File " + musicFilePath + " is invalid");
  }

  /**
   * Gets the {@link Validator} used to validate that audio files have all the required tags.
   *
   * @return the {@link Validator} used to validate that audio files have all the required tags
   */
  public Validator getValidator() {
    return validator;
  }

}
