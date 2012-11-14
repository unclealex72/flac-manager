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

package uk.co.unclealex.music.command.checkin;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
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
import uk.co.unclealex.music.action.AddArtworkAction;
import uk.co.unclealex.music.action.CoverArtAction;
import uk.co.unclealex.music.action.EncodeAction;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.action.LinkAction;
import uk.co.unclealex.music.action.MoveAction;
import uk.co.unclealex.music.command.AbstractCommandTest;
import uk.co.unclealex.music.command.CheckinCommand;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FileLocationFactory;
import uk.co.unclealex.music.message.MessageService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class CheckinCommandTest extends AbstractCommandTest<CheckinCommand> {

  /**
   * @param commandClass
   * @param guiceModule
   */
  public CheckinCommandTest() {
    super(CheckinCommand.class, new CheckinModule());
  }

  @Test
  public void testCheckinSuccess() throws IOException, InvalidDirectoriesException, URISyntaxException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(path);
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("queen - a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(
        Paths.get("queen - a night at the opera", "02 lazing on a sunday afternoon.flac"),
        "lazing_on_a_sunday_afternoon.json");
    musicFilesByPath.put(Paths.get("queen - jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls_uncovered.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(stagingDir.resolve())))).thenReturn(
        queenFileLocations);
    when(artworkSearchingService.findArtwork(musicFileFor("fat_bottomed_girls_uncovered.json"))).thenReturn(
        new URI("http://www.unclealex.co.uk/cover.jpg"));
    own(musicFileFor("death_on_two_legs.json"), brianMay);
    own(musicFileFor("lazing_on_a_sunday_afternoon.json"), brianMay);
    own(musicFileFor("fat_bottomed_girls_uncovered.json"), brianMay, freddieMercury);
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
    command.execute(Collections.singletonList(stagingDir.resolve().toString()));
    FileLocation originalDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "01 death on two legs dedicated to.flac"));
    FileLocation originalDeathOnTwoLegsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.mp3"));
    FileLocation newDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.flac"));
    FileLocation originalLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "02 lazing on a sunday afternoon.flac"));
    FileLocation originalLazingOnASundayAfternoonEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.mp3"));
    FileLocation newLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.flac"));
    FileLocation originalFatBottomedGirlsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("queen - jazz", "02 fat bottomed girls.flac"));
    FileLocation originalFatBottomedGirlsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get("Q", "Queen", "Jazz 01", "02 Fat Bottomed Girls.mp3"));
    FileLocation newFatBottomedGirlsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get("Q", "Queen", "Jazz 01", "02 Fat Bottomed Girls.flac"));
    assertThat("The wrong actions were recorded.", recordingActionExecutor.getExecutedActions(), contains(new Action[] {
        new AddArtworkAction(originalFatBottomedGirlsFlacLocation, new URI("http://www.unclealex.co.uk/cover.jpg")),
        new EncodeAction(
            originalDeathOnTwoLegsFlacLocation,
            originalDeathOnTwoLegsEncodedLocation,
            musicFileFor("death_on_two_legs.json")),
        new LinkAction(originalDeathOnTwoLegsEncodedLocation, brianMay),
        new MoveAction(originalDeathOnTwoLegsFlacLocation, newDeathOnTwoLegsFlacLocation),
        new EncodeAction(
            originalLazingOnASundayAfternoonFlacLocation,
            originalLazingOnASundayAfternoonEncodedLocation,
            musicFileFor("lazing_on_a_sunday_afternoon.json")),
        new LinkAction(originalLazingOnASundayAfternoonEncodedLocation, brianMay),
        new MoveAction(originalLazingOnASundayAfternoonFlacLocation, newLazingOnASundayAfternoonFlacLocation),
        new CoverArtAction(originalFatBottomedGirlsFlacLocation),
        new EncodeAction(
            originalFatBottomedGirlsFlacLocation,
            originalFatBottomedGirlsEncodedLocation,
            musicFileFor("fat_bottomed_girls_uncovered.json")),
        new LinkAction(originalFatBottomedGirlsEncodedLocation, brianMay, freddieMercury),
        new MoveAction(originalFatBottomedGirlsFlacLocation, newFatBottomedGirlsFlacLocation) }));
  }
  
  @Test
  public void testCheckinFailures() throws InvalidDirectoriesException, IOException, URISyntaxException {
    final FileLocationFactory fileLocationFactory = injector.getInstance(FileLocationFactory.class);
    final FileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Function<Path, FileLocation> queenFactory = new Function<Path, FileLocation>() {
      public FileLocation apply(Path path) {
        return fileLocationFactory.createStagingFileLocation(path);
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("queen - a night at the opera", "01 death on two legs dedicated to.flac"),
        "lazing_on_a_sunday_afternoon.json");
    musicFilesByPath.put(
        Paths.get("queen - a night at the opera", "02 lazing on a sunday afternoon.flac"),
        "lazing_on_a_sunday_afternoon.json");
    musicFilesByPath.put(Paths.get("queen - jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls_uncovered.json");
    SortedSet<FileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(stagingDir.resolve())))).thenReturn(
        queenFileLocations);
    own(musicFileFor("lazing_on_a_sunday_afternoon.json"), brianMay);
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
    command.execute(Collections.singletonList(stagingDir.resolve().toString()));
    FileLocation originalDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "01 death on two legs dedicated to.flac"));
    FileLocation originalLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "02 lazing on a sunday afternoon.flac"));
    FileLocation originalLazingOnASundayAfternoonEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.mp3"));
    FileLocation newLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.flac"));
    FileLocation originalFatBottomedGirlsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("queen - jazz", "02 fat bottomed girls.flac"));
    assertThat("The wrong actions were recorded.", recordingActionExecutor.getExecutedActions(), containsInAnyOrder(new Action[] {
        new FailureAction(originalFatBottomedGirlsFlacLocation, MessageService.NOT_OWNED),
        new FailureAction(originalFatBottomedGirlsFlacLocation, MessageService.MISSING_ARTWORK),
        new FailureAction(originalLazingOnASundayAfternoonEncodedLocation, MessageService.NON_UNIQUE, tracks(originalDeathOnTwoLegsFlacLocation, originalLazingOnASundayAfternoonFlacLocation)),
        new FailureAction(newLazingOnASundayAfternoonFlacLocation, MessageService.NON_UNIQUE, tracks(originalDeathOnTwoLegsFlacLocation, originalLazingOnASundayAfternoonFlacLocation))
    }));
  }
  
  protected SortedSet<FileLocation> tracks(FileLocation... fileLocations) {
    return Sets.newTreeSet(Arrays.asList(fileLocations));
  }
}
