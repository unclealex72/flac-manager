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

package uk.co.unclealex.music;

import java.io.IOException;

/**
 * An interface for classes that can manipulate {@link MusicFile}s.
 * @author alex
 *
 */
public interface MusicFileService {

  /**
   * Transfer the tags from one {@link MusicFile} to another.
   * @param source The source of all tags.
   * @param target The target for all tags.
   * @throws IOException 
   */
  public void transfer(MusicFile source, MusicFile target) throws IOException;
}
