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
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

/**
 * An interface for classes that can check whether a list of FLAC files will be moved to unique locations
 * during a check-in operation.
 * @author alex
 *
 */
public interface UniquenessCheckingService {

  /**
   * Calculate if any FLAC files will be moved to the same file.
   * @param relativeFlacFilenamesByOriginalPath A set of new flac file names keyed by the original file name.
   * @return A map of all the FLAC files that will be moved to a non-unique file name, keyed by that file name.
   */
  public Map<Path, Collection<Path>> listNonUniquePaths(SortedMap<Path, Path> relativeFlacFilenamesByOriginalPath);
}
