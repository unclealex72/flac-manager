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
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.configuration.JDevice;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JDirectoryService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.sync.JSynchroniser;
import uk.co.unclealex.music.sync.JSynchroniserFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * The default implementation of {@link JSynchroniserService}.
 * 
 * @author alex
 * 
 */
public class JSynchroniserServiceImpl implements JSynchroniserService {

  /**
   * The logger for logging errors.
   */
  private static final Logger log = LoggerFactory.getLogger(JSynchroniserServiceImpl.class);
  
  /**
   * The {@link ExecutorService} used to create threads to synchronise devices.
   */
  private final ExecutorService executorService;

  /**
   * The {@link uk.co.unclealex.music.files.JDirectoryService} used to list files in the devices
   * repositories.
   */
  private final JDirectoryService directoryService;

  /**
   * The {@link uk.co.unclealex.music.sync.JSynchroniserFactory} used to create {@link uk.co.unclealex.music.sync.JSynchroniser}s.
   */
  private final JSynchroniserFactory<JDevice> synchroniserFactory;

  /**
   * The {@link uk.co.unclealex.music.message.JMessageService} used to print messages to the user.
   */
  private final JMessageService messageService;

  /**
   * The {@link uk.co.unclealex.music.devices.JDeviceService} used to query devices.
   */
  private final JDeviceService deviceService;

  /**
   * Instantiates a new synchroniser service impl.
   * 
   * @param executorService
   *          the executor service
   * @param directoryService
   *          the directory service
   * @param synchroniserFactory
   *          the synchroniser factory
   * @param deviceService
   *          the device service
   * @param messageService
   *          the message service
   */
  @Inject
  public JSynchroniserServiceImpl(
          ExecutorService executorService,
          JDirectoryService directoryService,
          JSynchroniserFactory<JDevice> synchroniserFactory,
          JDeviceService deviceService,
          JMessageService messageService) {
    super();
    this.executorService = executorService;
    this.synchroniserFactory = synchroniserFactory;
    this.directoryService = directoryService;
    this.deviceService = deviceService;
    this.messageService = messageService;
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public void synchronise(Multimap<JUser, JDevice> connectedDevices) throws IOException {
    Multimap<JUser, JFileLocation> fileLocationsByOwner = HashMultimap.create();
    for (JUser owner : connectedDevices.keySet()) {
      Path deviceRepositoryBase = getDeviceService().getDeviceRepositoryBase(owner);
      fileLocationsByOwner.putAll(owner, getDirectoryService().listFiles(deviceRepositoryBase));
    }
    ExecutorService executorService = getExecutorService();
    List<Future<Void>> futures = Lists.newArrayList();
    for (Entry<JUser, JDevice> entry : connectedDevices.entries()) {
      JUser owner = entry.getKey();
      JDevice device = entry.getValue();
      futures.add(executorService.submit(new SynchronisingTask(owner, device, fileLocationsByOwner)));
    }
    for (Future<Void> future : futures) {
      try {
        future.get();
      }
      catch (InterruptedException | ExecutionException e) {
        log.error("An error occurred during synchronisation of a device.", e);
      }
    }
  }

  /**
   * A {@link Callable} that synchronises a device.
   */
  class SynchronisingTask implements Callable<Void> {

    /**
     * The owner of the device to be synchronised.
     */
    private final JUser owner;

    /**
     * The device to be synchronised.
     */
    private final JDevice device;

    /**
     * A set of device music file locations for each owner.
     */
    private final Multimap<JUser, JFileLocation> fileLocationsByOwner;

    /**
     * Instantiates a new synchronising task.
     * 
     * @param owner
     *          the owner
     * @param device
     *          the device
     * @param fileLocationsByOwner
     *          the file locations by owner
     */
    public SynchronisingTask(JUser owner, JDevice device, Multimap<JUser, JFileLocation> fileLocationsByOwner) {
      super();
      this.owner = owner;
      this.device = device;
      this.fileLocationsByOwner = fileLocationsByOwner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void call() throws IOException {
      printMessage(JMessageService.SYNCHRONISING);
      JSynchroniser synchroniser = getSynchroniserFactory().createSynchroniser(getOwner(), getDevice());
      synchroniser.synchronise(getFileLocationsByOwner());
      printMessage(JMessageService.DEVICE_SYNCHRONISED);
      return null;
    }

    protected void printMessage(String template) {
      getMessageService().printMessage(template, getOwner().getName(), getDevice().getName());
    }
    
    /**
     * Gets the owner of the device to be synchronised.
     * 
     * @return the owner of the device to be synchronised
     */
    public JUser getOwner() {
      return owner;
    }

    /**
     * Gets the device to be synchronised.
     * 
     * @return the device to be synchronised
     */
    public JDevice getDevice() {
      return device;
    }

    /**
     * Gets the a set of device music file locations for each owner.
     * 
     * @return the a set of device music file locations for each owner
     */
    public Multimap<JUser, JFileLocation> getFileLocationsByOwner() {
      return fileLocationsByOwner;
    }

  }

  /**
   * Gets the {@link ExecutorService} used to create threads to synchronise
   * devices.
   * 
   * @return the {@link ExecutorService} used to create threads to synchronise
   *         devices
   */
  public ExecutorService getExecutorService() {
    return executorService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.sync.JSynchroniserFactory} used to create {@link uk.co.unclealex.music.sync.JSynchroniser}s.
   * 
   * @return the {@link uk.co.unclealex.music.sync.JSynchroniserFactory} used to create {@link uk.co.unclealex.music.sync.JSynchroniser}
   *         s
   */
  public JSynchroniserFactory<JDevice> getSynchroniserFactory() {
    return synchroniserFactory;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.message.JMessageService} used to print messages to the user.
   * 
   * @return the {@link uk.co.unclealex.music.message.JMessageService} used to print messages to the user
   */
  public JMessageService getMessageService() {
    return messageService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JDirectoryService} used to list files in the devices
   * repositories.
   * 
   * @return the {@link uk.co.unclealex.music.files.JDirectoryService} used to list files in the devices
   *         repositories
   */
  public JDirectoryService getDirectoryService() {
    return directoryService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.devices.JDeviceService} used to query devices.
   * 
   * @return the {@link uk.co.unclealex.music.devices.JDeviceService} used to query devices
   */
  public JDeviceService getDeviceService() {
    return deviceService;
  }
}
