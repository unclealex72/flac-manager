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

package uk.co.unclealex.music.configuration;

import java.nio.file.Paths;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.unclealex.music.configuration.json.CowonX7DeviceBean;
import uk.co.unclealex.music.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.configuration.json.IpodDeviceBean;

/**
 * @author alex
 * 
 */
public class DeviceVisitorTest {

  @Test
  public void testIpodDevice() {
    test(new IpodDeviceBean("118118"), IpodDevice.class);
  }

  @Test
  public void testFileSystemDevice() {
    test(new FileSystemDeviceBean("name", "220220", Paths.get("abc")), FileSystemDevice.class);
  }

  @Test
  public void testCowonX7Device() {
    test(new CowonX7DeviceBean("333999"), CowonX7Device.class);
  }

  public void test(final Device device, final Class<? extends Device> expectedDeviceClass) {
    final DeviceVisitor<Class<? extends Device>> visitor = new DeviceVisitor.Default<Class<? extends Device>>() {

      @Override
      public Class<? extends Device> visit(final IpodDevice ipodDevice) {
        return IpodDevice.class;
      }

      @Override
      public Class<? extends Device> visit(final FileSystemDevice fileSystemDevice) {
        return FileSystemDevice.class;
      }

      @Override
      public Class<? extends Device> visit(final CowonX7Device cowonX7Device) {
        return CowonX7Device.class;
      }
    };
    final Class<? extends Device> actualDeviceClass = device.accept(visitor);
    Assert.assertEquals("The wrong class was returned.", expectedDeviceClass, actualDeviceClass);
  }
}
