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
import uk.co.unclealex.music.command.inject.CommonModule;
import uk.co.unclealex.music.command.inject.ExternalModule;
import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.sync.DeviceConnectionService;
import uk.co.unclealex.process.inject.PackageCheckingModule;

import com.google.common.collect.Multimap;
import com.lexicalscope.jewel.cli.CommandLineInterface;

/**
 * The command used to synchronise connected {@link Device}s with the device
 * repositories.
 * 
 * @author alex
 * 
 */
public class SyncCommand {

  /**
   * The {@link DeviceConnectionService} used to find what devices are connected.
   */
  private final DeviceConnectionService connectedDeviceService;

  /**
   * The {@link SynchroniserService} used to synchronise connected devices.
   */
  private final SynchroniserService synchroniserService;

  /**
   * The {@link MessageService} used to show messages to the end user.
   */
  private final MessageService messageService;

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
  public SyncCommand(
      DeviceConnectionService connectedDeviceService,
      SynchroniserService synchroniserService,
      MessageService messageService) {
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
   * @throws InvalidDirectoriesException
   *           the invalid directories exception
   */
  @Executable({ CommonModule.class, ExternalModule.class, PackageCheckingModule.class })
  public void execute(SyncCommandLine commandLine) throws IOException, InvalidDirectoriesException {
    Multimap<User, Device> connectedDevices = getConnectedDeviceService().listConnectedDevices();
    for (Entry<User, Device> entry : connectedDevices.entries()) {
      getMessageService().printMessage(
          MessageService.FOUND_DEVICE,
          entry.getKey().getName(),
          entry.getValue().getName());
    }
    getSynchroniserService().synchronise(connectedDevices);
  }

  /**
   * Gets the {@link DeviceConnectionService} used to find what devices are
   * connected.
   * 
   * @return the {@link DeviceConnectionService} used to find what devices are
   *         connected
   */
  public DeviceConnectionService getConnectedDeviceService() {
    return connectedDeviceService;
  }

  /**
   * Gets the {@link MessageService} used to show messages to the end user.
   * 
   * @return the {@link MessageService} used to show messages to the end user
   */
  public MessageService getMessageService() {
    return messageService;
  }

  /**
   * Gets the {@link SynchroniserService} used to synchronise connected devices.
   * 
   * @return the {@link SynchroniserService} used to synchronise connected
   *         devices
   */
  public SynchroniserService getSynchroniserService() {
    return synchroniserService;
  }

}

/**
 * The command line for the {@link SyncCommand}.
 * 
 * @author alex
 * 
 */
@CommandLineInterface(application = "flacman-sync")
interface SyncCommandLine extends StandardHelpCommandLine {

}