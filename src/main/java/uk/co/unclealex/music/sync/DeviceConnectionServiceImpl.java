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

package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.nio.file.Path;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.sync.scsi.ScsiId;

import com.google.common.collect.Multimap;

/**
 * @author alex
 *
 */
public class DeviceConnectionServiceImpl implements DeviceConnectionService {

  /**
   * {@inheritDoc}
   */
  @Override
  public Multimap<User, Device> listConnectedDevices() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path mount(String uuid) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unmount(Path path) throws IOException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ScsiId findScsiIdForUuid(String uuid) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path findMountPointForScsiId(ScsiId scsiId) {
    // TODO Auto-generated method stub
    return null;
  }

}
