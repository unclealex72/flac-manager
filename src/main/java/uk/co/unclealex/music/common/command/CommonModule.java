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

package uk.co.unclealex.music.common.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import uk.co.unclealex.music.common.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.common.audio.AudioMusicFileFactoryImpl;
import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.json.JsonConfigurationFactory;
import uk.co.unclealex.music.common.files.FileUtils;
import uk.co.unclealex.music.common.files.FileUtilsImpl;
import uk.co.unclealex.music.common.files.FilenameService;
import uk.co.unclealex.music.common.files.FilenameServiceImpl;
import uk.co.unclealex.music.common.files.FlacDirectoryService;
import uk.co.unclealex.music.common.files.FlacDirectoryServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * The Guice {@link Module} for components common to checking in and checking out.
 * @author alex
 *
 */
public abstract class CommonModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    bind(FileSystem.class).toInstance(FileSystems.getDefault());
    bind(AudioMusicFileFactory.class).to(AudioMusicFileFactoryImpl.class);
    bindConfiguration();
    bind(FilenameService.class).to(FilenameServiceImpl.class);
    bind(FileUtils.class).to(FileUtilsImpl.class);
    bind(FlacDirectoryService.class).to(FlacDirectoryServiceImpl.class);
    configureSpecifics();
  }

  /**
   * Bind command specific dependencies.
   */
  protected abstract void configureSpecifics();
  
  /**
   * Bind the {@link Configuration} to be used to a validated configuration found in <code>$HOME/.flacman.json</code>.
   * This method should be overriden for testing.
   */
  protected void bindConfiguration() {
    Path configurationPath = Paths.get(System.getProperty("user.home"), ".flacman.json");
    try (InputStream in = Files.newInputStream(configurationPath)) {
      Configuration configuration = new JsonConfigurationFactory().load(in);
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      factory.getValidator().validate(configuration);
      bind(Configuration.class).toInstance(configuration);
    }
    catch (IOException e) {
      addError("Could not read configuration file " + configurationPath);
    }
  }
}
