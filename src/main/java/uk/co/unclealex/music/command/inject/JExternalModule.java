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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.JValidator;
import uk.co.unclealex.music.action.JActionExecutor;
import uk.co.unclealex.music.action.JActionExecutorImpl;
import uk.co.unclealex.music.command.checkin.JEncodingService;
import uk.co.unclealex.music.command.checkin.JLameEncodingService;
import uk.co.unclealex.music.command.checkin.covers.AmazonArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsServiceImpl;
import uk.co.unclealex.music.command.checkin.process.JMappingService;
import uk.co.unclealex.music.command.checkin.process.JMappingServiceImpl;
import uk.co.unclealex.music.command.sync.JSynchroniserService;
import uk.co.unclealex.music.command.sync.JSynchroniserServiceImpl;
import uk.co.unclealex.music.configuration.JConfiguration;
import uk.co.unclealex.music.configuration.JCowonX7Device;
import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JFileSystemDevice;
import uk.co.unclealex.music.configuration.JIpodDevice;
import uk.co.unclealex.music.configuration.json.JsonConfigurationFactory;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JDirectoryServiceImpl;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzBaseResource;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzClient;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzClientImpl;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzRetryFilter;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzThrottleDelay;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzThrottleRetries;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzWebResourceFactory;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzWebResourceFactoryImpl;
import uk.co.unclealex.music.musicbrainz.JOwnerService;
import uk.co.unclealex.music.musicbrainz.JOwnerServiceImpl;
import uk.co.unclealex.music.sync.*;
import uk.co.unclealex.music.sync.JSynchroniserFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * A module that contains all bindings that rely on either external
 * configuration or non-java code.
 * 
 * @author alex
 * 
 */
public class JExternalModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    // Configuration
    bindConfiguration();
    // LAME Encoding.
    bind(JEncodingService.class).to(JLameEncodingService.class);
    // Amazon artwork
    bind(SignedRequestsService.class).to(SignedRequestsServiceImpl.class);
    bind(ArtworkSearchingService.class).to(AmazonArtworkSearchingService.class);
    // MusicBrainz ownership
    bind(JMusicBrainzWebResourceFactory.class).to(JMusicBrainzWebResourceFactoryImpl.class);
    bind(JMusicBrainzRetryFilter.class);
    bind(JMusicBrainzClient.class).to(JMusicBrainzClientImpl.class);
    bind(JOwnerService.class).to(JOwnerServiceImpl.class);
    bindConstant().annotatedWith(JMusicBrainzThrottleDelay.class).to(1000l);
    bindConstant().annotatedWith(JMusicBrainzThrottleRetries.class).to(5);
    bindConstant().annotatedWith(JMusicBrainzBaseResource.class).to("http://musicbrainz.org/ws/2/");
    // The action executor
    bind(JActionExecutor.class).to(JActionExecutorImpl.class);
    // Directories and files.
    bind(JDirectoryService.class).to(JDirectoryServiceImpl.class);
    bind(JMappingService.class).to(JMappingServiceImpl.class);
    // Device synchronisers.
    bind(new TypeLiteral<JSynchroniserFactory<JDevice>>() {
    }).to(JSynchroniserFactoryImpl.class);
    install(new FactoryModuleBuilder().implement(JSynchroniser.class, JIpodSynchroniser.class).build(
        new TypeLiteral<JSynchroniserFactory<JIpodDevice>>() {
        }));
    install(new FactoryModuleBuilder().implement(JSynchroniser.class, JFileSystemSynchroniser.class).build(
        new TypeLiteral<JSynchroniserFactory<JFileSystemDevice>>() {
        }));
    install(new FactoryModuleBuilder().implement(JSynchroniser.class, JCowonX7Synchroniser.class).build(
        new TypeLiteral<JSynchroniserFactory<JCowonX7Device>>() {
        }));
    bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
    bind(JSynchroniserService.class).to(JSynchroniserServiceImpl.class);
  }

  /**
   * The provider for binding the configuration.
   * 
   * @author alex
   * 
   */
  static class ConfigurationProvider implements Provider<JConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationProvider.class);

    /**
     * The {@link uk.co.unclealex.music.JValidator} used to validate the JSON configuration.
     */
    private final JValidator validator;

    /**
     * Instantiates a new configuration provider.
     * 
     * @param validator
     *          the validator
     */
    @Inject
    public ConfigurationProvider(final JValidator validator) {
      super();
      this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JConfiguration get() {
      final Path configurationPath = Paths.get(System.getProperty("user.home"), ".flacman.json");
      try (InputStream in = Files.newInputStream(configurationPath)) {
        final JConfiguration configuration = new JsonConfigurationFactory().load(in);
        return getValidator().validate(configuration, "The configuration is invalid");
      }
      catch (final ConstraintViolationException e) {
        for (final ConstraintViolation<?> cv : e.getConstraintViolations()) {
          log.error(cv.getPropertyPath() + ": " + cv.getMessage());
        }
        return null;
      }
      catch (final IOException e) {
        throw new IllegalStateException("Could not read configuration file ", e);
      }
    }

    /**
     * Gets the {@link uk.co.unclealex.music.JValidator} used to validate the JSON configuration.
     * 
     * @return the {@link uk.co.unclealex.music.JValidator} used to validate the JSON configuration
     */
    public JValidator getValidator() {
      return validator;
    }
  }

  /**
   * Create a {@link uk.co.unclealex.music.configuration.JConfiguration} to be used to a validated configuration
   * found in <code>$HOME/.flacman.json</code>. This method should be overriden
   * for testing.
   */
  protected void bindConfiguration() {
    bind(JConfiguration.class).toProvider(ConfigurationProvider.class).in(Singleton.class);
  }

}
