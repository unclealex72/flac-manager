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

package uk.co.unclealex.music.command.sync;

import java.io.IOException;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;

import com.google.common.collect.Multimap;

/**
 * An interface for classes that can synchronise a set of {@link Device}s.
 * 
 * @author alex
 * 
 */
public interface SynchroniserService {

  /**
   * Synchronise all connected devices.
   * 
   * @param connectedDevices
   *          A map of all connected devices and their owners.
   * @throws IOException 
   */
  public void synchronise(Multimap<User, Device> connectedDevices) throws IOException;

}
