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
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import uk.co.unclealex.executable.streams.Stderr;
import uk.co.unclealex.executable.streams.Stdout;
import uk.co.unclealex.music.configuration.MtpDevice;
import uk.co.unclealex.music.devices.DeviceService;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

/**
 * The {@link SynchroniserFactory} for mtp devices.
 * 
 * @author alex
 * 
 */
@PackagesRequired({ "mtpfs", "rsync", "pmount" })
public class MtpDeviceSynchroniserFactory extends BlockDeviceSynchroniserFactory<MtpDevice> {

  @Inject
  public MtpDeviceSynchroniserFactory(
      ProcessRequestBuilder processRequestBuilder,
      DeviceService deviceService,
      @Stdout PrintStream stdout,
      @Stderr PrintStream stderr) {
    super(processRequestBuilder, deviceService, stdout, stderr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Path createOrFindMountPoint(MtpDevice device) throws IOException {
    Path mountDir = Files.createTempDirectory("mtpfs-synchroniser-");
    getProcessRequestBuilder()
        .forCommand("mtpfs")
        .withArguments(mountDir.toString())
        .withStandardOutput(getStdout())
        .withStandardError(getStderr())
        .executeAndWait();
    return mountDir;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Path getMusicDirectoryRelativeToDevice(MtpDevice device) {
    return Paths.get("Music");
  }
}
