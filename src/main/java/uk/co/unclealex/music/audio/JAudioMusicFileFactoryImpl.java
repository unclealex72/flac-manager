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

package uk.co.unclealex.music.audio;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JValidator;

/**
 * The default implementation of {@link JAudioMusicFileFactory}.
 * @author alex
 *
 */
public class JAudioMusicFileFactoryImpl implements JAudioMusicFileFactory {

  /**
   * The {@link uk.co.unclealex.music.JValidator} used to validate that audio files have all the required tags.
   */
  private final JValidator validator;

  
  /**
   * Instantiates a new audio music file factory.
   *
   * @param validator the validator
   */
  @Inject
  public JAudioMusicFileFactoryImpl(JValidator validator) {
    super();
    this.validator = validator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JMusicFile loadAndValidate(Path musicFilePath) throws IOException, ConstraintViolationException {
    JMusicFile musicFile = load(musicFilePath);
    return getValidator().validate(musicFile, "File " + musicFilePath + " is invalid");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JAudioMusicFile load(Path musicFilePath) throws IOException {
    return new JAudioMusicFile(musicFilePath);
  }

  /**
   * Gets the {@link uk.co.unclealex.music.JValidator} used to validate that audio files have all the required tags.
   *
   * @return the {@link uk.co.unclealex.music.JValidator} used to validate that audio files have all the required tags
   */
  public JValidator getValidator() {
    return validator;
  }

}
