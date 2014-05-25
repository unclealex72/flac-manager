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

import uk.co.unclealex.music.JMusicFileService;
import uk.co.unclealex.music.JMusicFileServiceImpl;
import uk.co.unclealex.music.JValidator;
import uk.co.unclealex.music.JValidatorImpl;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JActionsImpl;
import uk.co.unclealex.music.audio.JAudioMusicFileFactory;
import uk.co.unclealex.music.audio.JAudioMusicFileFactoryImpl;
import uk.co.unclealex.music.command.checkin.covers.ArtworkService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkServiceImpl;
import uk.co.unclealex.music.command.inject.JCommonModule.ConfigurationAwareProvider.DirectoriesProvider;
import uk.co.unclealex.music.command.validation.JCommitOwnership;
import uk.co.unclealex.music.command.validation.JCommitOwnershipFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JFailuresOnly;
import uk.co.unclealex.music.command.validation.JFailuresOnlyFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JFindMissingCoverArt;
import uk.co.unclealex.music.command.validation.JFindMissingCoverArtFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JNoOverwriting;
import uk.co.unclealex.music.command.validation.JNoOverwritingFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JNoOwner;
import uk.co.unclealex.music.command.validation.JNoOwnerFlacFilesValidator;
import uk.co.unclealex.music.command.validation.JUnique;
import uk.co.unclealex.music.command.validation.JUniqueFlacFilesValidator;
import uk.co.unclealex.music.configuration.AmazonConfiguration;
import uk.co.unclealex.music.configuration.JConfiguration;
import uk.co.unclealex.music.configuration.JDirectories;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.devices.JDeviceServiceImpl;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.files.JFileLocationFactoryImpl;
import uk.co.unclealex.music.files.JFileUtils;
import uk.co.unclealex.music.files.JFileUtilsImpl;
import uk.co.unclealex.music.files.JFilenameService;
import uk.co.unclealex.music.files.JFilenameServiceImpl;
import uk.co.unclealex.music.files.JFlacFileChecker;
import uk.co.unclealex.music.files.JFlacFileCheckerImpl;
import uk.co.unclealex.music.files.JProtectionAwareFileUtils;
import uk.co.unclealex.music.files.JProtectionServiceImpl;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.message.JMessageServiceImpl;
import uk.co.unclealex.music.musicbrainz.JChangeOwnershipService;
import uk.co.unclealex.music.musicbrainz.JChangeOwnershipServiceImpl;
import uk.co.unclealex.music.sync.JDeviceConnectionService;
import uk.co.unclealex.music.sync.JDeviceConnectionServiceImpl;
import uk.co.unclealex.music.sync.JMounterService;
import uk.co.unclealex.music.sync.JMounterServiceImpl;
import uk.co.unclealex.music.sync.drive.JDriveUuidService;
import uk.co.unclealex.music.sync.drive.JDriveUuidServiceImpl;
import uk.co.unclealex.music.sync.drive.JLsscsiScsiService;
import uk.co.unclealex.music.sync.drive.JMountedDriveService;
import uk.co.unclealex.music.sync.drive.JMtabMountedDriveService;
import uk.co.unclealex.music.sync.drive.JScsiService;
import uk.co.unclealex.music.sync.scsi.JScsiIdFactory;
import uk.co.unclealex.music.sync.scsi.JScsiIdFactoryImpl;
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
public class JCommonModule extends AbstractModule {

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
    bind(JValidator.class).to(JValidatorImpl.class);
    bind(JAudioMusicFileFactory.class).to(JAudioMusicFileFactoryImpl.class);
    bind(JActions.class).to(JActionsImpl.class);
    bind(JDeviceService.class).to(JDeviceServiceImpl.class);
    bind(JFilenameService.class).to(JFilenameServiceImpl.class);
    bind(JFileUtils.class).toInstance(new JProtectionAwareFileUtils(new JFileUtilsImpl(), new JProtectionServiceImpl()));
    bind(JMessageService.class).to(JMessageServiceImpl.class);
    bind(ArtworkService.class).to(ArtworkServiceImpl.class);
    bind(JChangeOwnershipService.class).to(JChangeOwnershipServiceImpl.class);
    bind(JFlacFilesValidator.class).annotatedWith(JFindMissingCoverArt.class).to(
        JFindMissingCoverArtFlacFilesValidator.class);
    bind(JFlacFilesValidator.class).annotatedWith(JFailuresOnly.class).to(JFailuresOnlyFlacFilesValidator.class);
    bind(JFlacFilesValidator.class).annotatedWith(JNoOverwriting.class).to(JNoOverwritingFlacFilesValidator.class);
    bind(JFlacFilesValidator.class).annotatedWith(JUnique.class).to(JUniqueFlacFilesValidator.class);
    bind(JFlacFilesValidator.class).annotatedWith(JNoOwner.class).to(JNoOwnerFlacFilesValidator.class);
    bind(JFlacFilesValidator.class).annotatedWith(JCommitOwnership.class).to(JCommitOwnershipFlacFilesValidator.class);
    bind(JMusicFileService.class).to(JMusicFileServiceImpl.class);
    bind(JFileLocationFactory.class).to(JFileLocationFactoryImpl.class);
    bind(JFlacFileChecker.class).to(JFlacFileCheckerImpl.class);
    install(new ProcessRequestBuilderModule());
    bind(AmazonConfiguration.class).toProvider(AmazonConfigurationProvider.class);
    bind(JDirectories.class).toProvider(DirectoriesProvider.class);
    bind(new TypeLiteral<List<JUser>>() {
    }).toProvider(UsersProvider.class);
    bind(JDeviceConnectionService.class).to(JDeviceConnectionServiceImpl.class);
    bind(JScsiIdFactory.class).to(JScsiIdFactoryImpl.class);
    bind(JDriveUuidService.class).to(JDriveUuidServiceImpl.class);
    bind(JMounterService.class).to(JMounterServiceImpl.class);
    bind(JMountedDriveService.class).to(JMtabMountedDriveService.class);
    bind(JScsiService.class).to(JLsscsiScsiService.class);
  }

  /**
   * A base class for {@link Provider}s based upon the already provided.
   * 
   * @param <C>
   *          the generic type {@link uk.co.unclealex.music.configuration.JConfiguration} object.
   * @author alex
   */
  static abstract class ConfigurationAwareProvider<C> implements Provider<C> {

    /**
     * The {@link uk.co.unclealex.music.configuration.JConfiguration} object that has already been created.
     */
    private final JConfiguration configuration;

    /**
     * Instantiates a new configuration aware provider.
     * 
     * @param configuration
     *          the configuration
     */
    public ConfigurationAwareProvider(final JConfiguration configuration) {
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
     *          The {@link uk.co.unclealex.music.configuration.JConfiguration} that has already been created.
     * @return The object to be provided.
     */
    protected abstract C get(JConfiguration configuration);

    /**
     * Gets the {@link uk.co.unclealex.music.configuration.JConfiguration} object that has already been created.
     * 
     * @return the {@link uk.co.unclealex.music.configuration.JConfiguration} object that has already been created
     */
    public JConfiguration getConfiguration() {
      return configuration;
    }

    /**
     * The {@link Provider} for {@link uk.co.unclealex.music.configuration.JDirectories}.
     */
    static class DirectoriesProvider extends ConfigurationAwareProvider<JDirectories> {

      /**
       * Instantiates a new directories provider.
       * 
       * @param configuration
       *          the configuration
       */
      @Inject
      public DirectoriesProvider(final JConfiguration configuration) {
        super(configuration);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected JDirectories get(final JConfiguration configuration) {
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
    public AmazonConfigurationProvider(final JConfiguration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AmazonConfiguration get(final JConfiguration configuration) {
      return configuration.getAmazon();
    }
  }

  /**
   * The {@link Provider} for {@link List< uk.co.unclealex.music.configuration.JUser >}.
   */
  static class UsersProvider extends ConfigurationAwareProvider<List<JUser>> {

    /**
     * Instantiates a new users provider.
     * 
     * @param configuration
     *          the configuration
     */
    @Inject
    public UsersProvider(final JConfiguration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<JUser> get(final JConfiguration configuration) {
      return configuration.getUsers();
    }
  }
}
