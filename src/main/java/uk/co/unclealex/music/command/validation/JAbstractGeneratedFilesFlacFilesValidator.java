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

package uk.co.unclealex.music.command.validation;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import uk.co.unclealex.music.JMusicFile;
import uk.co.unclealex.music.action.JAction;
import uk.co.unclealex.music.action.JActionFunction;
import uk.co.unclealex.music.action.JActions;
import uk.co.unclealex.music.action.JEncodeAction;
import uk.co.unclealex.music.action.JMoveAction;
import uk.co.unclealex.music.files.JFileLocation;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * A base class for {@link JFlacFilesValidator}s that check on to be created
 * {@link uk.co.unclealex.music.files.JFileLocation}s.
 * 
 * @author alex
 * 
 */
public abstract class JAbstractGeneratedFilesFlacFilesValidator implements JFlacFilesValidator {

  /**
   * {@inheritDoc}
   */
  @Override
  public final JActions validate(Map<JFileLocation, JMusicFile> musicFilesByFlacPath, JActions actions) {
    Multimap<JFileLocation, JFileLocation> targetFileLocationsBySourceFileLocation = createMultimap();
    Function<JAction, JFileLocation> targetFunction = new JActionFunction<JFileLocation>(null) {
      protected JFileLocation visitAndReturn(JEncodeAction encodeAction) {
        return encodeAction.getEncodedFileLocation();
      }

      protected JFileLocation visitAndReturn(JMoveAction moveAction) {
        return moveAction.getTargetFileLocation();
      }
    };
    for (JAction action : actions) {
      JFileLocation targetFileLocation = targetFunction.apply(action);
      if (targetFileLocation != null) {
        targetFileLocationsBySourceFileLocation.put(action.getFileLocation(), targetFileLocation);
      }
    }
    return validate(musicFilesByFlacPath, targetFileLocationsBySourceFileLocation, actions);
  }

  protected Multimap<JFileLocation, JFileLocation> createMultimap() {
    Map<JFileLocation, Collection<JFileLocation>> map = Maps.newTreeMap();
    Supplier<? extends SortedSet<JFileLocation>> factory = new Supplier<SortedSet<JFileLocation>>() {
      @Override
      public SortedSet<JFileLocation> get() {
        return Sets.newTreeSet();
      }
    };
    Multimap<JFileLocation, JFileLocation> targetFileLocationsBySourceFileLocation =
        Multimaps.newSortedSetMultimap(map, factory);
    return targetFileLocationsBySourceFileLocation;
  }

  /**
   * Validate a list of {@link uk.co.unclealex.music.action.JActions}.
   * 
   * @param musicFilesByFlacPath
   *          A map of {@link uk.co.unclealex.music.JMusicFile}s keyed by the {@link uk.co.unclealex.music.files.JFileLocation} that
   *          created them.
   * @param targetFileLocationsBySourceFileLocation
   *          A map of target {@link uk.co.unclealex.music.files.JFileLocation}s keyed by the
   *          {@link uk.co.unclealex.music.files.JFileLocation} of the {@link uk.co.unclealex.music.action.JAction} from which the target
   *          was taken.
   * @param actions
   *          The already generated {@link uk.co.unclealex.music.action.JActions}.
   * @return An {@link uk.co.unclealex.music.action.JActions} that object contains all the supplied
   *         {@link uk.co.unclealex.music.action.JAction}s and any other required {@link uk.co.unclealex.music.action.JFailureAction}s.
   */
  protected abstract JActions validate(
      Map<JFileLocation, JMusicFile> musicFilesByFlacPath,
      Multimap<JFileLocation, JFileLocation> targetFileLocationsBySourceFileLocation,
      JActions actions);

}
