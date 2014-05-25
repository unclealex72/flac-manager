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
import uk.co.unclealex.music.command.JCheckinCommand;
import uk.co.unclealex.music.command.JCheckinCommandLine;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;
import uk.co.unclealex.music.message.JMessageService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class CheckinCommandTest extends AbstractCommandTest<JCheckinCommand> {

  /**
   * @param commandClass
   * @param guiceModule
   */
  public CheckinCommandTest() {
    super(JCheckinCommand.class, new JCheckinModule());
  }

  @Test
  public void testCheckinSuccess() throws IOException, JInvalidDirectoriesException, URISyntaxException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
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
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(stagingDir.resolve())))).thenReturn(
        queenFileLocations);
    when(artworkSearchingService.findArtwork(musicFileFor("fat_bottomed_girls_uncovered.json"))).thenReturn(
        new URI("http://www.unclealex.co.uk/cover.jpg"));
    own(musicFileFor("death_on_two_legs.json"), brianMay);
    own(musicFileFor("lazing_on_a_sunday_afternoon.json"), brianMay);
    own(musicFileFor("fat_bottomed_girls_uncovered.json"), brianMay, freddieMercury);
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
    command.execute(commandLine(Collections.singletonList(stagingDir.resolve().toString())));
    JFileLocation originalDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "01 death on two legs dedicated to.flac"));
    JFileLocation originalDeathOnTwoLegsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.mp3"));
    JFileLocation newDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.flac"));
    JFileLocation originalLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "02 lazing on a sunday afternoon.flac"));
    JFileLocation originalLazingOnASundayAfternoonEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.mp3"));
    JFileLocation newLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.flac"));
    JFileLocation originalFatBottomedGirlsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("queen - jazz", "02 fat bottomed girls.flac"));
    JFileLocation originalFatBottomedGirlsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get("Q", "Queen", "Jazz 01", "02 Fat Bottomed Girls.mp3"));
    JFileLocation newFatBottomedGirlsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get("Q", "Queen", "Jazz 01", "02 Fat Bottomed Girls.flac"));
    assertThat("The wrong actions were recorded.", recordingActionExecutor.getExecutedActions(), contains(new JAction[] {
        new JAddArtworkAction(originalFatBottomedGirlsFlacLocation, new URI("http://www.unclealex.co.uk/cover.jpg")),
        new JEncodeAction(
            originalDeathOnTwoLegsFlacLocation,
            originalDeathOnTwoLegsEncodedLocation,
            musicFileFor("death_on_two_legs.json")),
        new JLinkAction(originalDeathOnTwoLegsEncodedLocation, brianMay),
        new JMoveAction(originalDeathOnTwoLegsFlacLocation, newDeathOnTwoLegsFlacLocation),
        new JEncodeAction(
            originalLazingOnASundayAfternoonFlacLocation,
            originalLazingOnASundayAfternoonEncodedLocation,
            musicFileFor("lazing_on_a_sunday_afternoon.json")),
        new JLinkAction(originalLazingOnASundayAfternoonEncodedLocation, brianMay),
        new JMoveAction(originalLazingOnASundayAfternoonFlacLocation, newLazingOnASundayAfternoonFlacLocation),
        new JCoverArtAction(originalFatBottomedGirlsFlacLocation),
        new JEncodeAction(
            originalFatBottomedGirlsFlacLocation,
            originalFatBottomedGirlsEncodedLocation,
            musicFileFor("fat_bottomed_girls_uncovered.json")),
        new JLinkAction(originalFatBottomedGirlsEncodedLocation, brianMay, freddieMercury),
        new JMoveAction(originalFatBottomedGirlsFlacLocation, newFatBottomedGirlsFlacLocation) }));
  }
  
  /**
   * @param flacPaths
   * @return
   */
  protected JCheckinCommandLine commandLine(final List<String> flacPaths) {
    return new JCheckinCommandLine() {
      
      @Override
      public boolean getHelp() {
        return false;
      }
      
      @Override
      public List<String> getFlacPaths() {
        return flacPaths;
      }
    };
  }

  @Test
  public void testCheckinFailures() throws JInvalidDirectoriesException, IOException, URISyntaxException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation stagingDir = fileLocationFactory.createStagingFileLocation(Paths.get(""));
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
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
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(stagingDir), argThat(contains(stagingDir.resolve())))).thenReturn(
        queenFileLocations);
    own(musicFileFor("lazing_on_a_sunday_afternoon.json"), brianMay);
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
    command.execute(commandLine(Collections.singletonList(stagingDir.resolve().toString())));
    JFileLocation originalDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "01 death on two legs dedicated to.flac"));
    JFileLocation originalLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "queen - a night at the opera",
            "02 lazing on a sunday afternoon.flac"));
    JFileLocation originalLazingOnASundayAfternoonEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.mp3"));
    JFileLocation newLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.flac"));
    JFileLocation originalFatBottomedGirlsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get("queen - jazz", "02 fat bottomed girls.flac"));
    assertThat("The wrong actions were recorded.", recordingActionExecutor.getExecutedActions(), containsInAnyOrder(new JAction[] {
        new JFailureAction(originalFatBottomedGirlsFlacLocation, JMessageService.NOT_OWNED),
        new JFailureAction(originalFatBottomedGirlsFlacLocation, JMessageService.MISSING_ARTWORK),
        new JFailureAction(originalLazingOnASundayAfternoonEncodedLocation, JMessageService.NON_UNIQUE, tracks(originalDeathOnTwoLegsFlacLocation, originalLazingOnASundayAfternoonFlacLocation)),
        new JFailureAction(newLazingOnASundayAfternoonFlacLocation, JMessageService.NON_UNIQUE, tracks(originalDeathOnTwoLegsFlacLocation, originalLazingOnASundayAfternoonFlacLocation))
    }));
  }
  
  protected SortedSet<JFileLocation> tracks(JFileLocation... fileLocations) {
    return Sets.newTreeSet(Arrays.asList(fileLocations));
  }
}
