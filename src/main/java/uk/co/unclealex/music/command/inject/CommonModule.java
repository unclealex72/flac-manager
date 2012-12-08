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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.bridge.SLF4JBridgeHandler;

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
import uk.co.unclealex.music.command.inject.CommonModule.ConfigurationAwareProvider.DirectoriesProvider;
import uk.co.unclealex.music.command.validation.CommitOwnership;
import uk.co.unclealex.music.command.validation.CommitOwnershipFlacFilesValidator;
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
import uk.co.unclealex.music.configuration.AmazonConfiguration;
import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.Directories;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.music.devices.DeviceServiceImpl;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.files.FileLocationFactoryImpl;
import uk.co.unclealex.music.files.FileUtils;
import uk.co.unclealex.music.files.FileUtilsImpl;
import uk.co.unclealex.music.files.FilenameService;
import uk.co.unclealex.music.files.FilenameServiceImpl;
import uk.co.unclealex.music.files.FlacFileChecker;
import uk.co.unclealex.music.files.FlacFileCheckerImpl;
import uk.co.unclealex.music.files.ProtectionAwareFileUtils;
import uk.co.unclealex.music.files.ProtectionServiceImpl;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.message.MessageServiceImpl;
import uk.co.unclealex.music.musicbrainz.ChangeOwnershipService;
import uk.co.unclealex.music.musicbrainz.ChangeOwnershipServiceImpl;
import uk.co.unclealex.music.sync.DeviceConnectionService;
import uk.co.unclealex.music.sync.DeviceConnectionServiceImpl;
import uk.co.unclealex.music.sync.DriveUuidService;
import uk.co.unclealex.music.sync.DriveUuidServiceImpl;
import uk.co.unclealex.music.sync.scsi.ScsiIdFactory;
import uk.co.unclealex.music.sync.scsi.ScsiIdFactoryImpl;
import uk.co.unclealex.process.inject.ProcessRequestBuilderModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.mycila.inject.jsr250.Jsr250;

/**
 * The Guice {@link Module} for components common to all commands.
 * 
 * @author alex
 */
public class CommonModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    // Make sure java.util.logging is directed to SLF4J. This module is
    // guaranteed to be called so here's as good a place
    // as any.
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    install(Jsr250.newJsr250Module());
    bind(Validator.class).to(ValidatorImpl.class);
    bind(AudioMusicFileFactory.class).to(AudioMusicFileFactoryImpl.class);
    bind(Actions.class).to(ActionsImpl.class);
    bind(DeviceService.class).to(DeviceServiceImpl.class);
    bind(FilenameService.class).to(FilenameServiceImpl.class);
    bind(FileUtils.class).toInstance(new ProtectionAwareFileUtils(new FileUtilsImpl(), new ProtectionServiceImpl()));
    bind(MessageService.class).to(MessageServiceImpl.class);
    bind(ArtworkService.class).to(ArtworkServiceImpl.class);
    bind(ChangeOwnershipService.class).to(ChangeOwnershipServiceImpl.class);
    bind(FlacFilesValidator.class).annotatedWith(FindMissingCoverArt.class).to(
        FindMissingCoverArtFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(FailuresOnly.class).to(FailuresOnlyFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOverwriting.class).to(NoOverwritingFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(Unique.class).to(UniqueFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOwner.class).to(NoOwnerFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(CommitOwnership.class).to(CommitOwnershipFlacFilesValidator.class);
    bind(MusicFileService.class).to(MusicFileServiceImpl.class);
    bind(FileLocationFactory.class).to(FileLocationFactoryImpl.class);
    bind(FlacFileChecker.class).to(FlacFileCheckerImpl.class);
    install(new ProcessRequestBuilderModule());
    bind(AmazonConfiguration.class).toProvider(AmazonConfigurationProvider.class);
    bind(Directories.class).toProvider(DirectoriesProvider.class);
    bind(new TypeLiteral<List<User>>() {
    }).toProvider(UsersProvider.class);
    bind(DeviceConnectionService.class).to(DeviceConnectionServiceImpl.class);
    bind(ScsiIdFactory.class).to(ScsiIdFactoryImpl.class);
    bind(DriveUuidService.class).to(DriveUuidServiceImpl.class);
  }

  /**
   * A base class for {@link Provider}s based upon the already provided.
   * 
   * @param <C>
   *          the generic type {@link Configuration} object.
   * @author alex
   */
  static abstract class ConfigurationAwareProvider<C> implements Provider<C> {

    /**
     * The {@link Configuration} object that has already been created.
     */
    private final Configuration configuration;

    /**
     * Instantiates a new configuration aware provider.
     * 
     * @param configuration
     *          the configuration
     */
    public ConfigurationAwareProvider(final Configuration configuration) {
      super();
      this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C get() {
      return get(getConfiguration());
    }

    /**
     * Create the object to be provided.
     * 
     * @param configuration
     *          The {@link Configuration} that has already been created.
     * @return The object to be provided.
     */
    protected abstract C get(Configuration configuration);

    /**
     * Gets the {@link Configuration} object that has already been created.
     * 
     * @return the {@link Configuration} object that has already been created
     */
    public Configuration getConfiguration() {
      return configuration;
    }

    /**
     * The {@link Provider} for {@link Directories}.
     */
    static class DirectoriesProvider extends ConfigurationAwareProvider<Directories> {

      /**
       * Instantiates a new directories provider.
       * 
       * @param configuration
       *          the configuration
       */
      @Inject
      public DirectoriesProvider(final Configuration configuration) {
        super(configuration);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected Directories get(final Configuration configuration) {
        return configuration.getDirectories();
      }
    }
  }

  /**
   * The {@link Provider} for {@link AmazonConfiguration}.
   */
  static class AmazonConfigurationProvider extends ConfigurationAwareProvider<AmazonConfiguration> {

    /**
     * Instantiates a new amazon configuration provider.
     * 
     * @param configuration
     *          the configuration
     */
    @Inject
    public AmazonConfigurationProvider(final Configuration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AmazonConfiguration get(final Configuration configuration) {
      return configuration.getAmazon();
    }
  }

  /**
   * The {@link Provider} for {@link List<User>}.
   */
  static class UsersProvider extends ConfigurationAwareProvider<List<User>> {

    /**
     * Instantiates a new users provider.
     * 
     * @param configuration
     *          the configuration
     */
    @Inject
    public UsersProvider(final Configuration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> get(final Configuration configuration) {
      return configuration.getUsers();
    }
  }
}
