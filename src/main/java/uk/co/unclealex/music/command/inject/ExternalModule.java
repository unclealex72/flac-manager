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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.command.checkin.covers.AmazonArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsServiceImpl;
import uk.co.unclealex.music.command.checkout.EncodingService;
import uk.co.unclealex.music.command.checkout.LameEncodingService;
import uk.co.unclealex.music.command.inject.ExternalModule.ConfigurationAwareProvider.DirectoriesProvider;
import uk.co.unclealex.music.configuration.AmazonConfiguration;
import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.Directories;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.JsonConfigurationFactory;
import uk.co.unclealex.music.musicbrainz.MusicBrainzBaseResource;
import uk.co.unclealex.music.musicbrainz.MusicBrainzClient;
import uk.co.unclealex.music.musicbrainz.MusicBrainzClientImpl;
import uk.co.unclealex.music.musicbrainz.MusicBrainzRetryFilter;
import uk.co.unclealex.music.musicbrainz.MusicBrainzThrottleDelay;
import uk.co.unclealex.music.musicbrainz.MusicBrainzThrottleRetries;
import uk.co.unclealex.music.musicbrainz.MusicBrainzWebResourceFactory;
import uk.co.unclealex.music.musicbrainz.MusicBrainzWebResourceFactoryImpl;
import uk.co.unclealex.music.musicbrainz.OwnerService;
import uk.co.unclealex.music.musicbrainz.OwnerServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * A module that contains all bindings that rely on either external
 * configuration or non-java code.
 * 
 * @author alex
 * 
 */
public class ExternalModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    bindConfiguration();
    bind(AmazonConfiguration.class).toProvider(AmazonConfigurationProvider.class);
    bind(Directories.class).toProvider(DirectoriesProvider.class);
    bind(new TypeLiteral<List<User>>() {
    }).toProvider(UsersProvider.class);
    // LAME Encoding.
    bind(EncodingService.class).to(LameEncodingService.class);
    // Amazon artwork
    bind(SignedRequestsService.class).to(SignedRequestsServiceImpl.class);
    bind(ArtworkSearchingService.class).to(AmazonArtworkSearchingService.class);
    // MusicBrainz ownership
    bind(MusicBrainzWebResourceFactory.class).to(MusicBrainzWebResourceFactoryImpl.class);
    bind(MusicBrainzRetryFilter.class);
    bind(MusicBrainzClient.class).to(MusicBrainzClientImpl.class);
    bind(OwnerService.class).to(OwnerServiceImpl.class);
    bindConstant().annotatedWith(MusicBrainzThrottleDelay.class).to(1000l);
    bindConstant().annotatedWith(MusicBrainzThrottleRetries.class).to(5);
    bindConstant().annotatedWith(MusicBrainzBaseResource.class).to("http://musicbrainz.org/ws/2/");
  }

  /**
   * The provider for binding the configuration.
   * 
   * @author alex
   * 
   */
  static class ConfigurationProvider implements Provider<Configuration> {

    /**
     * The {@link Validator} used to validate the JSON configuration.
     */
    private final Validator validator;

    /**
     * Instantiates a new configuration provider.
     * 
     * @param validator
     *          the validator
     */
    @Inject
    public ConfigurationProvider(Validator validator) {
      super();
      this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration get() {
      Path configurationPath = Paths.get(System.getProperty("user.home"), ".flacman.json");
      try (InputStream in = Files.newInputStream(configurationPath)) {
        Configuration configuration = new JsonConfigurationFactory().load(in);
        return getValidator().validate(configuration, "The configuration is invalid");
      }
      catch (IOException e) {
        throw new IllegalStateException("Could not read configuration file ", e);
      }
    }

    /**
     * Gets the {@link Validator} used to validate the JSON configuration.
     * 
     * @return the {@link Validator} used to validate the JSON configuration
     */
    public Validator getValidator() {
      return validator;
    }
  }

  /**
   * Create a {@link Configuration} to be used to a validated configuration
   * found in <code>$HOME/.flacman.json</code>. This method should be overriden
   * for testing.
   */
  protected void bindConfiguration() {
    bind(Configuration.class).toProvider(ConfigurationProvider.class);
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
}
