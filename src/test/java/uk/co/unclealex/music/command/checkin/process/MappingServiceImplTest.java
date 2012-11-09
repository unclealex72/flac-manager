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

package uk.co.unclealex.music.command.checkin.process;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.SortedMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hamcrest.collection.IsEmptyIterable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.MusicFileBean;
import uk.co.unclealex.music.Validator;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.ActionsImpl;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.audio.AudioMusicFileFactory;
import uk.co.unclealex.music.files.FileLocation;
import uk.co.unclealex.music.files.FlacFileChecker;
import uk.co.unclealex.music.message.MessageService;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingServiceImplTest {

  @Mock
  AudioMusicFileFactory audioMusicFileFactory;
  @Mock
  Validator validator;
  @Mock
  FlacFileChecker flacFileChecker;
  MappingService mappingService;
  Supplier<Actions> actionsSupplier = new Supplier<Actions>() {
    @Override
    public Actions get() {
      return new ActionsImpl();
    }
  };
  FileLocation fileLocation = new FileLocation(Paths.get("/"), Paths.get("flac.flac"));

  @Mock
  ConstraintViolation<MusicFile> firstConstraintViolation;
  @Mock
  ConstraintViolation<MusicFile> secondConstraintViolation;

  @Before
  public void setup() {
    mappingService = new MappingServiceImpl(audioMusicFileFactory, validator, flacFileChecker);
  }

  @Test
  public void testValidFile() throws ConstraintViolationException, IOException {
    MusicFile musicFile = new MusicFileBean();
    Path musicPath = fileLocation.resolve();
    when(audioMusicFileFactory.load(musicPath)).thenReturn(musicFile);
    when(validator.generateViolations(musicFile)).thenReturn(new HashSet<ConstraintViolation<MusicFile>>());
    when(flacFileChecker.isFlacFile(musicPath)).thenReturn(true);
    SortedMap<FileLocation, MusicFile> musicFilesByFileLocation = Maps.newTreeMap();
    Actions actions =
        mappingService.mapPathsToMusicFiles(
            actionsSupplier.get(),
            Collections.singleton(fileLocation),
            musicFilesByFileLocation);
    assertThat("Actions were generated when none were expected.", actions, new IsEmptyIterable<>());
    assertThat("The wrong file locations were returned.", musicFilesByFileLocation.keySet(), contains(fileLocation));
    assertSame("The wrong music file was returned.", musicFile, musicFilesByFileLocation.get(fileLocation));
  }

  @Test
  public void testNonFlacFile() throws IOException {
    when(flacFileChecker.isFlacFile(fileLocation.resolve())).thenReturn(false);
    testActions(new FailureAction(fileLocation, MessageService.NOT_FLAC));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUntaggedFile() throws IOException {
    when(flacFileChecker.isFlacFile(fileLocation.resolve())).thenReturn(true);
    when(firstConstraintViolation.getMessage()).thenReturn("first");
    when(secondConstraintViolation.getMessage()).thenReturn("second");
    MusicFile musicFile = new MusicFileBean();
    when(audioMusicFileFactory.load(fileLocation.resolve())).thenReturn(musicFile);
    when(validator.generateViolations(musicFile)).thenReturn(
        Sets.newHashSet(firstConstraintViolation, secondConstraintViolation));
    testActions(new FailureAction(fileLocation, "/flac.flac: first"), new FailureAction(fileLocation, "/flac.flac: second"));
  }

  public void testActions(Action... expectedActions) throws IOException {
    SortedMap<FileLocation, MusicFile> musicFilesByFileLocation = Maps.newTreeMap();
    Actions actions =
        mappingService.mapPathsToMusicFiles(
            actionsSupplier.get(),
            Collections.singleton(fileLocation),
            musicFilesByFileLocation);
    assertThat("The wrong actions were returned.", actions.get(), containsInAnyOrder(expectedActions));
  }
}
