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

package uk.co.unclealex.music.command.own;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.*;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.command.AbstractCommandTest;
import uk.co.unclealex.music.command.JOwnCommand;
import uk.co.unclealex.music.command.JOwnCommandLine;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class OwnCommandTest extends AbstractCommandTest<JOwnCommand> {

  /**
   * @param commandClass
   * @param guiceModule
   */
  public OwnCommandTest() {
    super(JOwnCommand.class, new JOwnModule());
  }

  @Test
  public void testAdd() throws IOException, JInvalidDirectoriesException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    own(musicFileFor("death_on_two_legs.json"), brianMay);
    own(musicFileFor("fat_bottomed_girls.json"), brianMay, freddieMercury);
    Answer<JActions> mappingAnswer = new Answer<JActions>() {
      @Override
      public JActions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<JFileLocation, JMusicFile> musicFilesByFileLocation =
            (SortedMap<JFileLocation, JMusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (JActions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(JActions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, JFileLocation.class))),
            anyMapOf(JFileLocation.class, JMusicFile.class))).thenAnswer(mappingAnswer);
    JOwnCommandLine ownCommandLine = new JOwnCommandLine() {
      public boolean getHelp() {
        return false;
      }

      @Override
      public List<String> getFlacPaths() {
        return Collections.singletonList(queenDir.toString());
      }

      @Override
      public boolean isDisown() {
        return false;
      }

      @Override
      public String getOwners() {
        return "brian,freddie";
      }
    };
    command.execute(ownCommandLine);
    JFileLocation deathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "01 death on two legs dedicated to.flac"));
    JFileLocation oneVisionFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen", "a kind of magic", "01 one vision.flac"));
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        contains(new JAction[] {
                new JChangeOwnerAction(oneVisionFlacLocation, musicFileFor("one_vision.json"), true, Lists
                    .newArrayList((JUser) brianMay, freddieMercury)),
                    new JChangeOwnerAction(deathOnTwoLegsFlacLocation, musicFileFor("death_on_two_legs.json"), true, Lists.newArrayList(
                        (JUser)
                        freddieMercury)),
            new JUpdateOwnershipAction() }));
  }
  
  @Test
  public void testRemove() throws IOException, JInvalidDirectoriesException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    own(musicFileFor("death_on_two_legs.json"), brianMay, freddieMercury);
    own(musicFileFor("one_vision.json"), brianMay, freddieMercury);
    Answer<JActions> mappingAnswer = new Answer<JActions>() {
      @Override
      public JActions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<JFileLocation, JMusicFile> musicFilesByFileLocation =
            (SortedMap<JFileLocation, JMusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (JActions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(JActions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, JFileLocation.class))),
            anyMapOf(JFileLocation.class, JMusicFile.class))).thenAnswer(mappingAnswer);
    JOwnCommandLine ownCommandLine = new JOwnCommandLine() {
      public boolean getHelp() {
        return false;
      }

      @Override
      public List<String> getFlacPaths() {
        return Collections.singletonList(queenDir.toString());
      }

      @Override
      public boolean isDisown() {
        return true;
      }

      @Override
      public String getOwners() {
        return "brian";
      }
    };
    command.execute(ownCommandLine);
    JFileLocation deathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "01 death on two legs dedicated to.flac"));
    JFileLocation oneVisionFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen", "a kind of magic", "01 one vision.flac"));
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        contains(new JAction[] {
            new JChangeOwnerAction(oneVisionFlacLocation, musicFileFor("one_vision.json"), false, Lists
                .newArrayList((JUser) brianMay)),
            new JChangeOwnerAction(deathOnTwoLegsFlacLocation, musicFileFor("death_on_two_legs.json"), false, Lists.newArrayList(
                (JUser)
                brianMay)),
            new JUpdateOwnershipAction() }));
  }
  
  @Test
  public void testNoChange() throws IOException, JInvalidDirectoriesException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    Answer<JActions> mappingAnswer = new Answer<JActions>() {
      @Override
      public JActions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<JFileLocation, JMusicFile> musicFilesByFileLocation =
            (SortedMap<JFileLocation, JMusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (JActions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(JActions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, JFileLocation.class))),
            anyMapOf(JFileLocation.class, JMusicFile.class))).thenAnswer(mappingAnswer);
    JOwnCommandLine ownCommandLine = new JOwnCommandLine() {
      public boolean getHelp() {
        return false;
      }

      @Override
      public List<String> getFlacPaths() {
        return Collections.singletonList(queenDir.toString());
      }

      @Override
      public boolean isDisown() {
        return true;
      }

      @Override
      public String getOwners() {
        return "brian";
      }
    };
    command.execute(ownCommandLine);
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        emptyCollectionOf(JAction.class));
  }
  
  @Test
  public void testInvalidUser() throws IOException, JInvalidDirectoriesException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    Answer<JActions> mappingAnswer = new Answer<JActions>() {
      @Override
      public JActions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<JFileLocation, JMusicFile> musicFilesByFileLocation =
            (SortedMap<JFileLocation, JMusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (JActions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(JActions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, JFileLocation.class))),
            anyMapOf(JFileLocation.class, JMusicFile.class))).thenAnswer(mappingAnswer);
    JOwnCommandLine ownCommandLine = new JOwnCommandLine() {
      public boolean getHelp() {
        return false;
      }

      @Override
      public List<String> getFlacPaths() {
        return Collections.singletonList(queenDir.toString());
      }

      @Override
      public boolean isDisown() {
        return true;
      }

      @Override
      public String getOwners() {
        return "brain";
      }
    };
    command.execute(ownCommandLine);
    System.out.println(Joiner.on('\n').join(recordingActionExecutor.getExecutedActions()));
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        contains(new JAction[] { new JFailureAction(null, JMessageService.UNKNOWN_USER, "brain")}));
  }
}
