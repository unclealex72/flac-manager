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

package uk.co.unclealex.music.command.inject;

import uk.co.unclealex.music.MusicFileService;
import uk.co.unclealex.music.MusicFileServiceImpl;
import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.ValidatorImpl;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.audio.AudioMusicFileFactoryImpl;
import uk.co.unclealex.music.command.checkin.covers.ArtworkService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkServiceImpl;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.command.checkin.process.MappingServiceImpl;
import uk.co.unclealex.music.command.validation.FailuresOnly;
import uk.co.unclealex.music.command.validation.FailuresOnlyFlacFilesValidator;
import uk.co.unclealex.music.command.validation.FindMissingCoverArt;
import uk.co.unclealex.music.command.validation.FindMissingCoverArtFlacFilesValidator;
import uk.co.unclealex.music.command.validation.FlacFilesValidator;
import uk.co.unclealex.music.command.validation.NoOverwriting;
import uk.co.unclealex.music.command.validation.NoOverwritingFlacFilesValidator;
import uk.co.unclealex.music.command.validation.NoOwner;
import uk.co.unclealex.music.command.validation.NoOwnerFlacFilesValidator;
import uk.co.unclealex.music.command.validation.Unique;
import uk.co.unclealex.music.command.validation.UniqueFlacFilesValidator;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.devices.DeviceServiceImpl;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.files.DirectoryServiceImpl;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.files.FileLocationFactoryImpl;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.files.FileUtilsImpl;
import uk.co.unclealex.music.files.FilenameService;
import uk.co.unclealex.music.files.FilenameServiceImpl;
import uk.co.unclealex.music.files.FlacFileChecker;
import uk.co.unclealex.music.files.FlacFileCheckerImpl;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.message.MessageServiceImpl;
import uk.co.unclealex.process.inject.ProcessRequestBuilderModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.mycila.inject.jsr250.Jsr250;

/**
 * The Guice {@link Module} for components common to all commands.
 * @author alex
 *
 */
public class CommonModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    install(Jsr250.newJsr250Module());
    bind(Validator.class).to(ValidatorImpl.class);
    bind(AudioMusicFileFactory.class).to(AudioMusicFileFactoryImpl.class);
    bind(Actions.class).to(ActionsImpl.class);
    bind(DeviceService.class).to(DeviceServiceImpl.class);
    bind(FilenameService.class).to(FilenameServiceImpl.class);
    bind(FileUtils.class).to(FileUtilsImpl.class);
    bind(DirectoryService.class).to(DirectoryServiceImpl.class);
    bind(MappingService.class).to(MappingServiceImpl.class);
    bind(MessageService.class).to(MessageServiceImpl.class);
    bind(ArtworkService.class).to(ArtworkServiceImpl.class);
    bind(FlacFilesValidator.class).annotatedWith(FindMissingCoverArt.class).to(FindMissingCoverArtFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(FailuresOnly.class).to(FailuresOnlyFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOverwriting.class).to(NoOverwritingFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(Unique.class).to(UniqueFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOwner.class).to(NoOwnerFlacFilesValidator.class);
    bind(MusicFileService.class).to(MusicFileServiceImpl.class);
    bind(FileLocationFactory.class).to(FileLocationFactoryImpl.class);
    bind(FlacFileChecker.class).to(FlacFileCheckerImpl.class);
    install(new ProcessRequestBuilderModule());
  }
}
