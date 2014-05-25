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

package uk.co.unclealex.music.command.checkout;

import static org.hamcrest.Matchers.contains;
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
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JDeleteAction;
import uk.co.unclealex.music.action.JMoveAction;
import uk.co.unclealex.music.action.JUnlinkAction;
import uk.co.unclealex.music.command.AbstractCommandTest;
import uk.co.unclealex.music.command.JCheckoutCommand;
import uk.co.unclealex.music.command.JCheckoutCommandLine;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileLocationFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class CheckoutCommandTest extends AbstractCommandTest<JCheckoutCommand> {

  /**
   * @param commandClass
   * @param guiceModule
   */
  public CheckoutCommandTest() {
    super(JCheckoutCommand.class, new JCheckoutModule());
  }

  @Test
  public void testCheckout() throws IOException, JInvalidDirectoriesException {
    final JFileLocationFactory fileLocationFactory = injector.getInstance(JFileLocationFactory.class);
    final JFileLocation flacDir = fileLocationFactory.createFlacFileLocation(Paths.get(""));
    final Path queenDir = flacDir.resolve(Paths.get("q", "queen")).resolve();
    final Function<Path, JFileLocation> queenFactory = new Function<Path, JFileLocation>() {
      public JFileLocation apply(Path path) {
        return fileLocationFactory.createFlacFileLocation(Paths.get("q", "queen").resolve(path));
      }
    };
    final Map<Path, String> musicFilesByPath = Maps.newHashMap();
    musicFilesByPath.put(
        Paths.get("a night at the opera", "01 death on two legs dedicated to.flac"),
        "death_on_two_legs.json");
    musicFilesByPath.put(
        Paths.get("a night at the opera", "02 lazing on a sunday afternoon.flac"),
        "lazing_on_a_sunday_afternoon.json");
    musicFilesByPath.put(Paths.get("jazz", "02 fat bottomed girls.flac"), "fat_bottomed_girls.json");
    SortedSet<JFileLocation> queenFileLocations =
        Sets.newTreeSet(Iterables.transform(musicFilesByPath.keySet(), queenFactory));
    when(directoryService.listFiles(eq(flacDir), argThat(contains(queenDir)))).thenReturn(queenFileLocations);
    own(musicFileFor("death_on_two_legs.json"), brianMay);
    own(musicFileFor("lazing_on_a_sunday_afternoon.json"), brianMay);
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
    JCheckoutCommandLine checkoutCommandLine = new JCheckoutCommandLine() {
      @Override
      public boolean getHelp() {
        return false;
      }
      @Override
      public List<String> getFlacPaths() {
        return Collections.singletonList(queenDir.toString());
      }
    };
    command.execute(checkoutCommandLine);
    JFileLocation originalDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "01 death on two legs dedicated to.flac"));
    JFileLocation originalDeathOnTwoLegsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.mp3"));
    JFileLocation newDeathOnTwoLegsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "01 Death on Two Legs Dedicated to.flac"));
    JFileLocation originalLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "q",
            "queen",
            "a night at the opera",
            "02 lazing on a sunday afternoon.flac"));
    JFileLocation originalLazingOnASundayAfternoonEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.mp3"));
    JFileLocation newLazingOnASundayAfternoonFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "Q",
            "Queen",
            "A Night at the Opera 01",
            "02 Lazing on a Sunday Afternoon.flac"));
    JFileLocation originalFatBottomedGirlsFlacLocation =
        fileLocationFactory.createFlacFileLocation(Paths.get(
            "q",
            "queen",
            "jazz",
            "02 fat bottomed girls.flac"));
    JFileLocation originalFatBottomedGirlsEncodedLocation =
        fileLocationFactory.createEncodedFileLocation(Paths.get(
            "Q",
            "Queen",
            "Jazz 01",
            "02 Fat Bottomed Girls.mp3"));
    JFileLocation newFatBottomedGirlsFlacLocation =
        fileLocationFactory.createStagingFileLocation(Paths.get(
            "Q",
            "Queen",
            "Jazz 01",
            "02 Fat Bottomed Girls.flac"));
    assertThat("The wrong actions were recorded.", recordingActionExecutor.getExecutedActions(), contains(new JAction[] {
        new JMoveAction(originalDeathOnTwoLegsFlacLocation, newDeathOnTwoLegsFlacLocation),
        new JUnlinkAction(originalDeathOnTwoLegsEncodedLocation, Sets.newHashSet((JUser) brianMay)),
        new JDeleteAction(originalDeathOnTwoLegsEncodedLocation),
        new JMoveAction(originalLazingOnASundayAfternoonFlacLocation, newLazingOnASundayAfternoonFlacLocation),
        new JUnlinkAction(originalLazingOnASundayAfternoonEncodedLocation, Sets.newHashSet((JUser) brianMay)),
        new JDeleteAction(originalLazingOnASundayAfternoonEncodedLocation),
        new JMoveAction(originalFatBottomedGirlsFlacLocation, newFatBottomedGirlsFlacLocation),
        new JUnlinkAction(originalFatBottomedGirlsEncodedLocation, Sets.newHashSet((JUser) brianMay, (JUser) freddieMercury)),
        new JDeleteAction(originalFatBottomedGirlsEncodedLocation)
    }));
  }
}
