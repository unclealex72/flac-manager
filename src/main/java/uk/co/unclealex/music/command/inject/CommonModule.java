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

import uk.co.unclealex.music.MusicFileService;
import uk.co.unclealex.music.MusicFileServiceImpl;
import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.ValidatorImpl;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.audio.AudioMusicFileFactoryImpl;
import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.command.checkin.covers.ArtworkService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkServiceImpl;
import uk.co.unclealex.music.command.inject.CommonModule.ConfigurationAwareProvider.DirectoriesProvider;
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
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.message.MessageServiceImpl;
import uk.co.unclealex.process.inject.ProcessRequestBuilderModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.mycila.inject.jsr250.Jsr250;

/**
 * The Guice {@link Module} for components common to all commands. That is,
 * everything but their {@link Execution}.
 *
 * @param <E> the element type
 * @author alex
 */
public abstract class CommonModule<E extends Execution> extends AbstractModule {

  /**
   * The class of the {@link Execution} to use in the command.
   */
  private final Class<E> executionClass;
  
  /**
   * Instantiates a new common module.
   *
   * @param executionClass the execution class
   */
  public CommonModule(Class<E> executionClass) {
    super();
    this.executionClass = executionClass;
  }

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
    bind(MessageService.class).to(MessageServiceImpl.class);
    bind(ArtworkService.class).to(ArtworkServiceImpl.class);
    bind(FlacFilesValidator.class).annotatedWith(FindMissingCoverArt.class).to(
        FindMissingCoverArtFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(FailuresOnly.class).to(FailuresOnlyFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOverwriting.class).to(NoOverwritingFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(Unique.class).to(UniqueFlacFilesValidator.class);
    bind(FlacFilesValidator.class).annotatedWith(NoOwner.class).to(NoOwnerFlacFilesValidator.class);
    bind(MusicFileService.class).to(MusicFileServiceImpl.class);
    bind(FileLocationFactory.class).to(FileLocationFactoryImpl.class);
    bind(FlacFileChecker.class).to(FlacFileCheckerImpl.class);
    bind(Execution.class).to(getExecutionClass());
    install(new ProcessRequestBuilderModule());
    bind(AmazonConfiguration.class).toProvider(AmazonConfigurationProvider.class);
    bind(Directories.class).toProvider(DirectoriesProvider.class);
    bind(new TypeLiteral<List<User>>() {}).toProvider(UsersProvider.class);
  }

  /**
   * A base class for {@link Provider}s based upon the already provided
   * {@link Configuration} object.
   * 
   * @param <C>
   *          the generic type
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
    public ConfigurationAwareProvider(Configuration configuration) {
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
      public DirectoriesProvider(Configuration configuration) {
        super(configuration);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected Directories get(Configuration configuration) {
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
    public AmazonConfigurationProvider(Configuration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AmazonConfiguration get(Configuration configuration) {
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
    public UsersProvider(Configuration configuration) {
      super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> get(Configuration configuration) {
      return configuration.getUsers();
    }
  }

  /**
   * Gets the class of the {@link Execution} to use in the command.
   *
   * @return the class of the {@link Execution} to use in the command
   */
  public Class<E> getExecutionClass() {
    return executionClass;
  }

  
}
