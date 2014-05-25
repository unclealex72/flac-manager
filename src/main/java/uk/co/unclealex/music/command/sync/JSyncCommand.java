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
import java.util.Map.Entry;

import javax.inject.Inject;

import uk.co.unclealex.executable.Executable;
import uk.co.unclealex.executable.StandardHelpCommandLine;
import uk.co.unclealex.music.command.inject.JCommonModule;
import uk.co.unclealex.music.command.inject.JExternalModule;
import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.exception.JInvalidDirectoriesException;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.sync.JDeviceConnectionService;
import uk.co.unclealex.process.inject.PackageCheckingModule;

import com.google.common.collect.Multimap;
import com.lexicalscope.jewel.cli.CommandLineInterface;

/**
 * The command used to synchronise connected {@link uk.co.unclealex.music.configuration.JDevice}s with the device
 * repositories.
 * 
 * @author alex
 * 
 */
public class JSyncCommand {

  /**
   * The {@link uk.co.unclealex.music.sync.JDeviceConnectionService} used to find what devices are connected.
   */
  private final JDeviceConnectionService connectedDeviceService;

  /**
   * The {@link JSynchroniserService} used to synchronise connected devices.
   */
  private final JSynchroniserService synchroniserService;

  /**
   * The {@link uk.co.unclealex.music.message.JMessageService} used to show messages to the end user.
   */
  private final JMessageService messageService;

  /**
   * Instantiates a new sync command.
   * 
   * @param connectedDeviceService
   *          the connected device service
   * @param synchroniserService
   *          the synchroniser service
   * @param messageService
   *          the message service
   */
  @Inject
  public JSyncCommand(
          JDeviceConnectionService connectedDeviceService,
          JSynchroniserService synchroniserService,
          JMessageService messageService) {
    super();
    this.connectedDeviceService = connectedDeviceService;
    this.synchroniserService = synchroniserService;
    this.messageService = messageService;
  }

  /**
   * Execute.
   * 
   * @param commandLine
   *          the command line
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws uk.co.unclealex.music.exception.JInvalidDirectoriesException
   *           the invalid directories exception
   */
  @Executable({ JCommonModule.class, JExternalModule.class, PackageCheckingModule.class })
  public void execute(JSyncCommandLine commandLine) throws IOException, JInvalidDirectoriesException {
    Multimap<JUser, JDevice> connectedDevices = getConnectedDeviceService().listConnectedDevices();
    for (Entry<JUser, JDevice> entry : connectedDevices.entries()) {
      getMessageService().printMessage(
          JMessageService.FOUND_DEVICE,
          entry.getKey().getName(),
          entry.getValue().getName());
    }
    getSynchroniserService().synchronise(connectedDevices);
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.JDeviceConnectionService} used to find what devices are
   * connected.
   * 
   * @return the {@link uk.co.unclealex.music.sync.JDeviceConnectionService} used to find what devices are
   *         connected
   */
  public JDeviceConnectionService getConnectedDeviceService() {
    return connectedDeviceService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.message.JMessageService} used to show messages to the end user.
   * 
   * @return the {@link uk.co.unclealex.music.message.JMessageService} used to show messages to the end user
   */
  public JMessageService getMessageService() {
    return messageService;
  }

  /**
   * Gets the {@link JSynchroniserService} used to synchronise connected devices.
   * 
   * @return the {@link JSynchroniserService} used to synchronise connected
   *         devices
   */
  public JSynchroniserService getSynchroniserService() {
    return synchroniserService;
  }

}

/**
 * The command line for the {@link JSyncCommand}.
 * 
 * @author alex
 * 
 */
@CommandLineInterface(application = "flacman-sync")
interface JSyncCommandLine extends StandardHelpCommandLine {

}