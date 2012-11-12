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

import uk.co.unclealex.music.MusicFile;
import uk.co.unclealex.music.action.Action;
import uk.co.unclealex.music.action.ActionFunction;
import uk.co.unclealex.music.action.Actions;
import uk.co.unclealex.music.action.EncodeAction;
import uk.co.unclealex.music.action.FailureAction;
import uk.co.unclealex.music.action.LinkAction;
import uk.co.unclealex.music.action.MoveAction;
import uk.co.unclealex.music.action.UnlinkAction;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * A base class for {@link FlacFilesValidator}s that check on to be created
 * {@link FileLocation}s.
 * 
 * @author alex
 * 
 */
public abstract class AbstractGeneratedFilesFlacFilesValidator implements FlacFilesValidator {

  /**
   * {@inheritDoc}
   */
  @Override
  public final Actions validate(Map<FileLocation, MusicFile> musicFilesByFlacPath, Actions actions) {
    Multimap<FileLocation, FileLocation> targetFileLocationsBySourceFileLocation = createMultimap();
    Function<Action, FileLocation> targetFunction = new ActionFunction<FileLocation>(null) {
      protected FileLocation visitAndReturn(EncodeAction encodeAction) {
        return encodeAction.getEncodedFileLocation();
      }

      protected FileLocation visitAndReturn(MoveAction moveAction) {
        return moveAction.getTargetFileLocation();
      }
      
      protected FileLocation visitAndReturn(LinkAction linkAction) {
        return linkAction.getLinkLocation();
      }

      protected FileLocation visitAndReturn(UnlinkAction unlinkAction) {
        return unlinkAction.getLinkLocation();
      }
};
    for (Action action : actions) {
      FileLocation targetFileLocation = targetFunction.apply(action);
      if (targetFileLocation != null) {
        targetFileLocationsBySourceFileLocation.put(action.getFileLocation(), targetFileLocation);
      }
    }
    return validate(musicFilesByFlacPath, targetFileLocationsBySourceFileLocation, actions);
  }

  protected Multimap<FileLocation, FileLocation> createMultimap() {
    Map<FileLocation, Collection<FileLocation>> map = Maps.newTreeMap();
    Supplier<? extends SortedSet<FileLocation>> factory = new Supplier<SortedSet<FileLocation>>() {
      @Override
      public SortedSet<FileLocation> get() {
        return Sets.newTreeSet();
      }
    };
    Multimap<FileLocation, FileLocation> targetFileLocationsBySourceFileLocation =
        Multimaps.newSortedSetMultimap(map, factory);
    return targetFileLocationsBySourceFileLocation;
  }

  /**
   * Validate a list of {@link Actions}.
   * 
   * @param musicFilesByFlacPath
   *          A map of {@link MusicFile}s keyed by the {@link FileLocation} that
   *          created them.
   * @param targetFileLocationsBySourceFileLocation
   *          A map of target {@link FileLocation}s keyed by the
   *          {@link FileLocation} of the {@link Action} from which the target
   *          was taken.
   * @param actions
   *          The already generated {@link Actions}.
   * @return An {@link Actions} that object contains all the supplied
   *         {@link Action}s and any other required {@link FailureAction}s.
   */
  protected abstract Actions validate(
      Map<FileLocation, MusicFile> musicFilesByFlacPath,
      Multimap<FileLocation, FileLocation> targetFileLocationsBySourceFileLocation,
      Actions actions);

}
