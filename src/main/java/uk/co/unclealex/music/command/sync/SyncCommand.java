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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.executable.Executable;
import uk.co.unclealex.executable.StandardHelpCommandLine;
import uk.co.unclealex.music.command.inject.CommonModule;
import uk.co.unclealex.music.command.inject.ExternalModule;
import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.exception.InvalidDirectoriesException;
import uk.co.unclealex.music.message.MessageService;
import uk.co.unclealex.music.sync.ConnectedDeviceService;
import uk.co.unclealex.music.sync.Synchroniser;
import uk.co.unclealex.music.sync.SynchroniserFactory;
import uk.co.unclealex.process.inject.PackageCheckingModule;

import com.google.common.collect.Multimap;
import com.lexicalscope.jewel.cli.CommandLineInterface;

/**
 * The command used to synchronise connected {@link Device}s with the device repositories.
 * @author alex
 *
 */
public class SyncCommand {

  /**
   * The logger to use for reporting synchronisation errors.
   */
  private static final Logger log = LoggerFactory.getLogger(SyncCommand.class);
  
  /**
   * The {@link ConnectedDeviceService} used to find what devices are connected.
   */
  private final ConnectedDeviceService connectedDeviceService;
  
  /**
   * The {@link SynchroniserFactory} used to create {@link Synchroniser}s.
   */
  private final SynchroniserFactory<Device> synchroniserFactory;
  
  /**
   * The {@link MessageService} used to show messages to the end user.
   */
  private final MessageService messageService;
  
  @Inject
  public SyncCommand(
      ConnectedDeviceService connectedDeviceService,
      SynchroniserFactory<Device> synchroniserFactory,
      MessageService messageService) {
    super();
    this.connectedDeviceService = connectedDeviceService;
    this.synchroniserFactory = synchroniserFactory;
    this.messageService = messageService;
  }


  /**
   * 
   * @param commandLine
   * @throws IOException
   * @throws InvalidDirectoriesException
   */
  @Executable({ CommonModule.class, ExternalModule.class, PackageCheckingModule.class })
  public void execute(SyncCommandLine commandLine) throws IOException, InvalidDirectoriesException {
    Multimap<User, Device> connectedDevices = getConnectedDeviceService().listConnectedDevices();
    for (Entry<User, Device> entry : connectedDevices.entries()) {
      printMessage(MessageService.FOUND_DEVICE, entry.getKey(), entry.getValue());
    }
    for (Entry<User, Device> entry : connectedDevices.entries()) {
      synchronise(entry.getKey(), entry.getValue());
    }
  }

  protected void printMessage(String template, User user, Device device) {
    getMessageService().printMessage(template, user.getName(), device.getName());
  }
  /**
   * @param key
   * @param value
   */
  protected void synchronise(User user, Device device) {
    printMessage(MessageService.SYNCHRONISING, user, device);
    Synchroniser synchroniser = getSynchroniserFactory().createSynchroniser(user, device);
    try {
      synchroniser.synchronise();
      printMessage(MessageService.DEVICE_SYNCHRONISED, user, device);
    }
    catch (Exception e) {
      log.error("Synchronising failed.", e);
    }
  }


  public ConnectedDeviceService getConnectedDeviceService() {
    return connectedDeviceService;
  }


  public SynchroniserFactory<Device> getSynchroniserFactory() {
    return synchroniserFactory;
  }


  public MessageService getMessageService() {
    return messageService;
  }

}

/**
 * The command line for the {@link SyncCommand}.
 * @author alex
 *
 */
@CommandLineInterface(application="flacman-sync")
interface SyncCommandLine extends StandardHelpCommandLine {
  
}