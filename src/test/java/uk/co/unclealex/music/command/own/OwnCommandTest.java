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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ChangeOwnerAction;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.action.UpdateOwnershipAction;
import uk.co.unclealex.music.command.AbstractCommandTest;
import uk.co.unclealex.music.command.OwnCommand;
import uk.co.unclealex.music.command.OwnCommandLine;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.message.MessageService;

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
public class OwnCommandTest extends AbstractCommandTest<OwnCommand> {

  /**
   * @param commandClass
   * @param guiceModule
   */
  public OwnCommandTest() {
    super(OwnCommand.class, new OwnModule());
  }

  @Test
  public void testAdd() throws IOException, InvalidDirectoriesException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    own(musicFileFor("death_on_two_legs.json"), brianMay);
    own(musicFileFor("fat_bottomed_girls.json"), brianMay, freddieMercury);
    Answer<Actions> mappingAnswer = new Answer<Actions>() {
      @Override
      public Actions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<FileLocation, MusicFile> musicFilesByFileLocation =
            (SortedMap<FileLocation, MusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (Actions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(Actions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, FileLocation.class))),
            anyMapOf(FileLocation.class, MusicFile.class))).thenAnswer(mappingAnswer);
    OwnCommandLine ownCommandLine = new OwnCommandLine() {
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
    FileLocation deathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "01 death on two legs dedicated to.flac"));
    FileLocation oneVisionFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen", "a kind of magic", "01 one vision.flac"));
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        contains(new Action[] {
                new ChangeOwnerAction(oneVisionFlacLocation, musicFileFor("one_vision.json"), true, Lists
                    .newArrayList((User) brianMay, freddieMercury)),
                    new ChangeOwnerAction(deathOnTwoLegsFlacLocation, musicFileFor("death_on_two_legs.json"), true, Lists.newArrayList(
                        (User)
                        freddieMercury)),
            new UpdateOwnershipAction() }));
  }
  
  @Test
  public void testRemove() throws IOException, InvalidDirectoriesException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    own(musicFileFor("death_on_two_legs.json"), brianMay, freddieMercury);
    own(musicFileFor("one_vision.json"), brianMay, freddieMercury);
    Answer<Actions> mappingAnswer = new Answer<Actions>() {
      @Override
      public Actions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<FileLocation, MusicFile> musicFilesByFileLocation =
            (SortedMap<FileLocation, MusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (Actions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(Actions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, FileLocation.class))),
            anyMapOf(FileLocation.class, MusicFile.class))).thenAnswer(mappingAnswer);
    OwnCommandLine ownCommandLine = new OwnCommandLine() {
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
    FileLocation deathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "01 death on two legs dedicated to.flac"));
    FileLocation oneVisionFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen", "a kind of magic", "01 one vision.flac"));
    assertThat(
        "The wrong actions were recorded.",
        recordingActionExecutor.getExecutedActions(),
        contains(new Action[] {
            new ChangeOwnerAction(oneVisionFlacLocation, musicFileFor("one_vision.json"), false, Lists
                .newArrayList((User) brianMay)),
            new ChangeOwnerAction(deathOnTwoLegsFlacLocation, musicFileFor("death_on_two_legs.json"), false, Lists.newArrayList(
                (User)
                brianMay)),
            new UpdateOwnershipAction() }));
  }
  
  @Test
  public void testNoChange() throws IOException, InvalidDirectoriesException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    Answer<Actions> mappingAnswer = new Answer<Actions>() {
      @Override
      public Actions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<FileLocation, MusicFile> musicFilesByFileLocation =
            (SortedMap<FileLocation, MusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (Actions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(Actions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, FileLocation.class))),
            anyMapOf(FileLocation.class, MusicFile.class))).thenAnswer(mappingAnswer);
    OwnCommandLine ownCommandLine = new OwnCommandLine() {
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
        emptyCollectionOf(Action.class));
  }
  
  @Test
  public void testInvalidUser() throws IOException, InvalidDirectoriesException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Path queenDir = stagingDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(Paths.get("a kind of magic", "01 one vision.flac"), "one_vision.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    Answer<Actions> mappingAnswer = new Answer<Actions>() {
      @Override
      public Actions answer(InvocationOnMock invocation) throws IOException {
        @SuppressWarnings("unchecked")
        SortedMap<FileLocation, MusicFile> musicFilesByFileLocation =
            (SortedMap<FileLocation, MusicFile>) invocation.getArguments()[2];
        for (Entry<Path, String> entry : musicFilesByPath.entrySet()) {
          musicFilesByFileLocation.put(queenFactory.apply(entry.getKey()), musicFileFor(entry.getValue()));
        }
        return (Actions) invocation.getArguments()[0];
      }
    };
    when(
        mappingService.mapPathsToMusicFiles(
            any(Actions.class),
            argThat(contains(Iterables.toArray(queenFileLocations, FileLocation.class))),
            anyMapOf(FileLocation.class, MusicFile.class))).thenAnswer(mappingAnswer);
    OwnCommandLine ownCommandLine = new OwnCommandLine() {
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
        contains(new Action[] { new FailureAction(null, MessageService.UNKNOWN_USER, "brain")}));
  }
}
