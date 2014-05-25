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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.unclealex.executable.streams.Stdout;
import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.JMusicFileBean;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionExecutor;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.process.JMappingService;
import uk.co.unclealex.music.configuration.JConfiguration;
import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.configuration.json.JConfigurationBean;
import uk.co.unclealex.music.configuration.json.JPathsBean;
import uk.co.unclealex.music.configuration.json.JUserBean;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.musicbrainz.JMusicBrainzClient;
import uk.co.unclealex.music.musicbrainz.JOwnerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCommandTest<C extends JCommand<?>> {

  private final Class<C> commandClass;
  private final Module guiceModule;

  public AbstractCommandTest(Class<C> commandClass, Module guiceModule) {
    super();
    this.commandClass = commandClass;
    this.guiceModule = guiceModule;
  }

  ObjectReader musicFileReader = new ObjectMapper().reader(JMusicFileBean.class);

  public Injector injector;
  public Path tmpDir;
  public JConfiguration configuration;
  public JUserBean freddieMercury = new JUserBean("freddie", "FreddieMercury", "pwd", new ArrayList<JDevice>());
  public JUserBean brianMay = new JUserBean("brian", "BrianMay", "pwd", new ArrayList<JDevice>());

  private MapBasedOwnerService ownerService;
  @Mock
  public ArtworkSearchingService artworkSearchingService;
  @Mock
  public JMappingService mappingService;
  @Mock
  public JDirectoryService directoryService;
  @Mock public JMusicBrainzClient musicBrainzClient;
  
  public RecordingActionExecutor recordingActionExecutor;
  public StringWriter stdout;

  public C command;

  @Before
  public void setup() throws IOException {
    tmpDir = Paths.get("/root");
    Path flacPath = tmpDir.resolve("flac");
    Path devicesPath = tmpDir.resolve("devices");
    Path encodedPath = tmpDir.resolve("encoded");
    Path stagingPath = tmpDir.resolve("staging");
    ownerService = new MapBasedOwnerService();
    configuration =
        new JConfigurationBean(new JPathsBean(flacPath, devicesPath, encodedPath, stagingPath), Arrays.asList(
            brianMay,
            freddieMercury), null);
    recordingActionExecutor = new RecordingActionExecutor();
    stdout = new StringWriter();
    Module testingModule = new AbstractModule() {

      @Override
      protected void configure() {
        bind(JConfiguration.class).toInstance(configuration);
        bind(ArtworkSearchingService.class).toInstance(artworkSearchingService);
        bind(JOwnerService.class).toInstance(ownerService);
        bind(JActionExecutor.class).toInstance(recordingActionExecutor);
        bind(getCommandClass());
        bind(JMappingService.class).toInstance(mappingService);
        bind(JDirectoryService.class).toInstance(directoryService);
        bind(JMusicBrainzClient.class).toInstance(musicBrainzClient);
        bind(PrintWriter.class).annotatedWith(Stdout.class).toInstance(new PrintWriter(stdout));
      }
    };
    injector = Guice.createInjector(getGuiceModule(), testingModule);
    command = injector.getInstance(getCommandClass());
  }

  protected JMusicFile musicFileFor(String resourceName) throws IOException {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
      return musicFileReader.readValue(in);
    }
  }

  public void own(JMusicFile musicFile, JUser... users) {
    ownerService.owners.put(musicFile, Sets.newHashSet(users));
  }
  
  static public class RecordingActionExecutor implements JActionExecutor {

    /**
     * A list of actions that have been passed to this executor.
     */
    private final List<JAction> executedActions = Lists.newArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(JAction action) throws IOException {
      getExecutedActions().add(action);
    }

    public List<JAction> getExecutedActions() {
      return executedActions;
    }
  }

  class MapBasedOwnerService implements JOwnerService {

    final Map<JMusicFile, Set<JUser>> owners = Maps.newHashMap();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<JUser> getOwnersForMusicFile(JMusicFile musicFile) {
      Set<JUser> users = owners.get(musicFile);
      return users == null ? new HashSet<JUser>() : users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFileOwnedByAnyone(JMusicFile musicFile) {
      return owners.containsKey(musicFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<JUser> getAllInvalidOwners() {
      return new HashSet<JUser>();
    }

    public Map<JMusicFile, Set<JUser>> getOwners() {
      return owners;
    }
    
  }
  public Class<C> getCommandClass() {
    return commandClass;
  }

  public Module getGuiceModule() {
    return guiceModule;
  }
}
