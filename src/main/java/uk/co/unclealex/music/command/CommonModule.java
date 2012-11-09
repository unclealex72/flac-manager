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

package uk.co.unclealex.music.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.ValidatorImpl;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.audio.AudioMusicFileFactoryImpl;
import uk.co.unclealex.music.command.checkin.covers.AmazonArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkServiceImpl;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsServiceImpl;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.command.checkin.process.MappingServiceImpl;
import uk.co.unclealex.music.command.checkout.EncodingService;
import uk.co.unclealex.music.command.checkout.LameEncodingService;
import uk.co.unclealex.music.command.validation.FailuresOnly;
import uk.co.unclealex.music.command.validation.FailuresOnlyFlacFilesValidator;
import uk.co.unclealex.music.command.validation.FindMissingCoverArt;
import uk.co.unclealex.music.command.validation.FindMissingCoverArtFlacFilesValidator;
import uk.co.unclealex.music.command.validation.FlacFilesValidator;
import uk.co.unclealex.music.command.validation.FlacFilesValidatorList;
import uk.co.unclealex.music.command.validation.NoOverwriting;
import uk.co.unclealex.music.command.validation.NoOverwritingFlacFilesValidator;
import uk.co.unclealex.music.command.validation.Unique;
import uk.co.unclealex.music.command.validation.UniqueFlacFilesValidator;
import uk.co.unclealex.music.configuration.AmazonConfiguration;
import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.Directories;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.JsonConfigurationFactory;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.DirectoryServiceImpl;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.files.FileUtilsImpl;
import uk.co.unclealex.music.files.FilenameService;
import uk.co.unclealex.music.files.FilenameServiceImpl;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.message.MessageServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

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
    bind(Validator.class).to(ValidatorImpl.class);
    bind(FileSystem.class).toInstance(FileSystems.getDefault());
    bind(AudioMusicFileFactory.class).to(AudioMusicFileFactoryImpl.class);
    bind(Actions.class).to(ActionsImpl.class);
    bindConfiguration();
    bind(FilenameService.class).to(FilenameServiceImpl.class);
    bind(FileUtils.class).to(FileUtilsImpl.class);
    bind(DirectoryService.class).to(DirectoryServiceImpl.class);
    bind(MappingService.class).to(MappingServiceImpl.class);
    bind(MessageService.class).to(MessageServiceImpl.class);
    bind(EncodingService.class).to(LameEncodingService.class);
    bind(SignedRequestsService.class).to(SignedRequestsServiceImpl.class);
    bind(ArtworkService.class).to(ArtworkServiceImpl.class);
    bind(ArtworkSearchingService.class).to(AmazonArtworkSearchingService.class);
    bind(FlacFilesValidator.class).annotatedWith(FindMissingCoverArt.class).to(FindMissingCoverArtFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(FailuresOnly.class).to(FailuresOnlyFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOverwriting.class).to(NoOverwritingFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(Unique.class).to(UniqueFlacFilesValidator.class);
    bind(new TypeLiteral<List<FlacFilesValidator>>() {}).to(FlacFilesValidatorList.class);
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
      bind(AmazonConfiguration.class).toInstance(configuration.getAmazon());
      bind(Directories.class).toInstance(configuration.getDirectories());
      bind(new TypeLiteral<List<User>>() {}).toInstance(configuration.getUsers());
    }
    catch (IOException e) {
      addError("Could not read configuration file " + configurationPath);
    }
  }
}
