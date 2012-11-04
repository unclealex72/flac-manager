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

package uk.co.unclealex.music.checkin.process;

import java.nio.file.Path;
import java.util.SortedMap;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import uk.co.unclealex.music.common.MusicFile;
import uk.co.unclealex.music.common.files.Extension;
import uk.co.unclealex.music.common.files.FilenameService;

/**
 * The default implementation of {@link FilenameCalculatingService}.
 * @author alex
 *
 */
public class FilenameCalculatingServiceImpl implements FilenameCalculatingService {

  /**
   * The {@link FilenameService} used to calculate individual file names.
   */
  private final FilenameService filenameService;
  
  @Inject
  public FilenameCalculatingServiceImpl(FilenameService filenameService) {
    super();
    this.filenameService = filenameService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedMap<Path, Path> calculateNewFilenames(SortedMap<Path, MusicFile> musicFilesByFlacPath) {
    final FilenameService filenameService = getFilenameService();
    Function<MusicFile, Path> filenameFunction = new Function<MusicFile, Path>() {
      public Path apply(MusicFile musicFile) {
        return filenameService.toPath(musicFile, Extension.FLAC);
      }
    };
    return Maps.transformValues(musicFilesByFlacPath, filenameFunction);
  }


  public FilenameService getFilenameService() {
    return filenameService;
  }

}
