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
import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionExecutor;
import uk.co.unclealex.music.command.checkin.covers.ArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.process.MappingService;
import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.configuration.json.PathsBean;
import uk.co.unclealex.music.configuration.json.UserBean;
import uk.co.unclealex.music.files.DirectoryService;
import uk.co.unclealex.music.musicbrainz.OwnerService;

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
public abstract class AbstractCommandTest<C extends Command<?>> {

  private final Class<C> commandClass;
  private final Module guiceModule;

  public AbstractCommandTest(Class<C> commandClass, Module guiceModule) {
    super();
    this.commandClass = commandClass;
    this.guiceModule = guiceModule;
  }

  ObjectReader musicFileReader = new ObjectMapper().reader(MusicFileBean.class);

  public Injector injector;
  public Path tmpDir;
  public Configuration configuration;
  public UserBean freddieMercury = new UserBean("freddie", "FreddieMercury", "pwd", new ArrayList<Device>());
  public UserBean brianMay = new UserBean("brian", "BrianMay", "pwd", new ArrayList<Device>());

  private MapBasedOwnerService ownerService;
  @Mock
  public ArtworkSearchingService artworkSearchingService;
  @Mock
  public MappingService mappingService;
  @Mock
  public DirectoryService directoryService;
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
        new ConfigurationBean(new PathsBean(flacPath, devicesPath, encodedPath, stagingPath), Arrays.asList(
            brianMay,
            freddieMercury), null);
    recordingActionExecutor = new RecordingActionExecutor();
    stdout = new StringWriter();
    Module testingModule = new AbstractModule() {

      @Override
      protected void configure() {
        bind(Configuration.class).toInstance(configuration);
        bind(ArtworkSearchingService.class).toInstance(artworkSearchingService);
        bind(OwnerService.class).toInstance(ownerService);
        bind(ActionExecutor.class).toInstance(recordingActionExecutor);
        bind(getCommandClass());
        bind(MappingService.class).toInstance(mappingService);
        bind(DirectoryService.class).toInstance(directoryService);
        bind(PrintWriter.class).annotatedWith(Stdout.class).toInstance(new PrintWriter(stdout));
      }
    };
    injector = Guice.createInjector(getGuiceModule(), testingModule);
    command = injector.getInstance(getCommandClass());
  }

  protected MusicFile musicFileFor(String resourceName) throws IOException {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {
      return musicFileReader.readValue(in);
    }
  }

  public void own(MusicFile musicFile, User... users) {
    ownerService.owners.put(musicFile, Sets.newHashSet(users));
  }
  
  static public class RecordingActionExecutor implements ActionExecutor {

    /**
     * A list of actions that have been passed to this executor.
     */
    private final List<Action> executedActions = Lists.newArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Action action) throws IOException {
      getExecutedActions().add(action);
    }

    public List<Action> getExecutedActions() {
      return executedActions;
    }
  }

  class MapBasedOwnerService implements OwnerService {

    final Map<MusicFile, Set<User>> owners = Maps.newHashMap();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<User> getOwnersForMusicFile(MusicFile musicFile) {
      Set<User> users = owners.get(musicFile);
      return users == null ? new HashSet<User>() : users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFileOwnedByAnyone(MusicFile musicFile) {
      return owners.containsKey(musicFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<User> getAllInvalidOwners() {
      return new HashSet<User>();
    }

    public Map<MusicFile, Set<User>> getOwners() {
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
