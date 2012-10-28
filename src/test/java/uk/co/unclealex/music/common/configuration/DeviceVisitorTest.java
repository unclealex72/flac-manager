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

package uk.co.unclealex.music.common.configuration;

import java.nio.file.Paths;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.unclealex.music.common.configuration.Device;
import uk.co.unclealex.music.common.configuration.DeviceVisitor;
import uk.co.unclealex.music.common.configuration.FileSystemDevice;
import uk.co.unclealex.music.common.configuration.IpodDevice;
import uk.co.unclealex.music.common.configuration.MtpDevice;
import uk.co.unclealex.music.common.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.common.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.common.configuration.json.MtpDeviceBean;

/**
 * @author alex
 *
 */
public class DeviceVisitorTest {

  @Test
  public void testIpodDevice() {
    test(new IpodDeviceBean(Paths.get("abc")), IpodDevice.class);
  }

  @Test
  public void testFileSystemDevice() {
    test(new FileSystemDeviceBean("name", Paths.get("abc"), Paths.get("abc")), FileSystemDevice.class);
  }

  @Test
  public void testMtpDevice() {
    test(new MtpDeviceBean("name"), MtpDevice.class);
  }
  
  public void test(Device device, Class<? extends Device> expectedDeviceClass) {
    DeviceVisitor<Class<? extends Device>> visitor = new DeviceVisitor.Default<Class<? extends Device>>() {

      @Override
      public Class<? extends Device> visit(IpodDevice ipodDevice) {
        return IpodDevice.class;
      }

      @Override
      public Class<? extends Device> visit(FileSystemDevice fileSystemDevice) {
        return FileSystemDevice.class;
      }

      @Override
      public Class<? extends Device> visit(MtpDevice mtpDevice) {
        return MtpDevice.class;
      }
    };
    Class<? extends Device> actualDeviceClass = device.accept(visitor);
    Assert.assertEquals("The wrong class was returned.", expectedDeviceClass,  actualDeviceClass);
  }
}
