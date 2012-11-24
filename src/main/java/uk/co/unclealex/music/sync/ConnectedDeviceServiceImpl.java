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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.DeviceVisitor;
import uk.co.unclealex.music.configuration.FileSystemDevice;
import uk.co.unclealex.music.configuration.IpodDevice;
import uk.co.unclealex.music.configuration.MountedDevice;
import uk.co.unclealex.music.configuration.MtpDevice;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.process.AbstractProcessCallback;
import uk.co.unclealex.process.ProcessCallback;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * The default implementation of {@link ConnectedDeviceService}.
 * 
 * @author alex
 * 
 */
@PackagesRequired("usbutils")
public class ConnectedDeviceServiceImpl implements ConnectedDeviceService {

  /**
   * The list of known users.
   */
  private final List<User> users;

  /**
   * The {@link ProcessRequestBuilder} used to run native processes.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  @Inject
  public ConnectedDeviceServiceImpl(List<User> users, ProcessRequestBuilder processRequestBuilder) {
    super();
    this.users = users;
    this.processRequestBuilder = processRequestBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Multimap<User, Device> listConnectedDevices() throws IOException {
    HashMultimap<User, Device> connectedDevices = HashMultimap.create();
    List<String> connectedUsbDevices = list(5, "lsusb");
    List<String> mountedDevices = list(1, "cat", "/etc/mtab");
    for (User user : getUsers()) {
      for (Device device : user.getDevices()) {
        if (isDeviceConnected(device, connectedUsbDevices, mountedDevices)) {
          connectedDevices.put(user, device);
        }
      }
    }
    return connectedDevices;
  }

  /**
   * Check to see if a device is connected.
   * 
   * @param device
   *          The device to check.
   * @param connectedUsbDevices
   *          The list of USB IDs connected to this machine.
   * @param mountedDevices
   *          The list of mount points where physical devices are connected.
   * @return True if the device is connected, false otherwise.
   */
  protected boolean isDeviceConnected(
      Device device,
      final List<String> connectedUsbDevices,
      final List<String> mountedDevices) {
    DeviceVisitor<Boolean> visitor = new DeviceVisitor.Default<Boolean>() {

      @Override
      public Boolean visit(IpodDevice ipodDevice) {
        return isMounted(ipodDevice);
      }

      @Override
      public Boolean visit(FileSystemDevice fileSystemDevice) {
        return isMounted(fileSystemDevice);
      }

      @Override
      public Boolean visit(MtpDevice mtpDevice) {
        return connectedUsbDevices.contains(mtpDevice.getUsbId());
      }

      protected boolean isMounted(MountedDevice mountedDevice) {
        return mountedDevices.contains(mountedDevice.getMountPoint().toString());
      }
    };
    return device.accept(visitor);
  }

  /**
   * Run a command and return a column from its output.
   * 
   * @param columnToReturn
   *          The number of the column to return.
   * @param command
   *          The command to run.
   * @param arguments
   *          The arguments to supply to the command.
   * @return A list of strings, each containing a column from the command's
   *         output.
   * @throws IOException
   */
  protected List<String> list(final int columnToReturn, String command, String... arguments) throws IOException {
    final List<String> cells = Lists.newArrayList();
    ProcessCallback callback = new AbstractProcessCallback() {
      @Override
      public void lineWritten(String line) {
        String cell =
            Iterables.get(
                Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().trimResults().split(line),
                columnToReturn,
                null);
        if (cell != null) {
          cells.add(processOctal(cell));
        }
      }
    };
    executeProcess(callback, command, arguments);
    return cells;
  }

  /**
   * Generate a process request.
   * @param callback
   * @param command
   * @param arguments
   * @return
   * @throws IOException 
   */
  protected void executeProcess(
      ProcessCallback callback,
      String command,
      String... arguments) throws IOException {
    getProcessRequestBuilder().forCommand(command).withArguments(arguments).withCallbacks(callback).executeAndWait();
  }

  /**
   * Convert octal escaped characters in strings.
   * @param line The line to convert.
   * @return A new string that contains normal characters 
   */
  protected String processOctal(String line) {
    Pattern octalPattern = Pattern.compile("\\\\([0-7]{3})");
    StringBuffer sb = new StringBuffer();
    Matcher matcher = octalPattern.matcher(line);
    while (matcher.find()) {
      char ch = (char) Integer.parseInt(matcher.group(1), 8);
      matcher.appendReplacement(sb, new String(new char[] { ch }));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  public List<User> getUsers() {
    return users;
  }

  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }

}
