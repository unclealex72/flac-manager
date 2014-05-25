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

import uk.co.unclealex.music.configuration.json.JCowonX7DeviceBean;
import uk.co.unclealex.music.configuration.json.JFileSystemDeviceBean;
import uk.co.unclealex.music.configuration.json.JIpodDeviceBean;

/**
 * @author alex
 * 
 */
public class DeviceVisitorTest {

  @Test
  public void testIpodDevice() {
    test(new JIpodDeviceBean("118118"), JIpodDevice.class);
  }

  @Test
  public void testFileSystemDevice() {
    test(new JFileSystemDeviceBean("name", "220220", Paths.get("abc")), JFileSystemDevice.class);
  }

  @Test
  public void testCowonX7Device() {
    test(new JCowonX7DeviceBean("333999"), JCowonX7Device.class);
  }

  public void test(final JDevice device, final Class<? extends JDevice> expectedDeviceClass) {
    final JDeviceVisitor<Class<? extends JDevice>> visitor = new JDeviceVisitor.Default<Class<? extends JDevice>>() {

      @Override
      public Class<? extends JDevice> visit(final JIpodDevice ipodDevice) {
        return JIpodDevice.class;
      }

      @Override
      public Class<? extends JDevice> visit(final JFileSystemDevice fileSystemDevice) {
        return JFileSystemDevice.class;
      }

      @Override
      public Class<? extends JDevice> visit(final JCowonX7Device cowonX7Device) {
        return JCowonX7Device.class;
      }
    };
    final Class<? extends JDevice> actualDeviceClass = device.accept(visitor);
    Assert.assertEquals("The wrong class was returned.", expectedDeviceClass, actualDeviceClass);
  }
}
