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

package uk.co.unclealex.music.common.configuration.json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.ConfigurationFactory;
import uk.co.unclealex.music.common.configuration.Device;
import uk.co.unclealex.music.common.configuration.json.AmazonConfigurationBean;
import uk.co.unclealex.music.common.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.common.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.common.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.common.configuration.json.JsonConfigurationFactory;
import uk.co.unclealex.music.common.configuration.json.MtpDeviceBean;
import uk.co.unclealex.music.common.configuration.json.PathsBean;
import uk.co.unclealex.music.common.configuration.json.UserBean;

import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class JsonConfigurationFactoryTest {

  @Test
  public void testDeserialisation() throws IOException {
    PathsBean expectedDirectories =
        new PathsBean(
            Paths.get("flacPath"),
            Paths.get("devicesPath"),
            Paths.get("encodedPath"),
            Paths.get("stagingPath"));
    Device ipod = new IpodDeviceBean(Paths.get("/media/IPOD"));
    Device mtp = new MtpDeviceBean("mtp device");
    Device hd = new FileSystemDeviceBean("hd device", Paths.get("mountPoint"), Paths.get("music"));
    UserBean alex = new UserBean("alex", "pwd", Lists.newArrayList(ipod, mtp, hd));
    List<UserBean> expectedUsers = Lists.newArrayList(alex);
    AmazonConfigurationBean expectedAmazon = new AmazonConfigurationBean("endpoint", "accessKey", "secretKey");
    ConfigurationBean expectedConfigurationBean = new ConfigurationBean(expectedDirectories, expectedUsers, expectedAmazon);

    ConfigurationFactory configurationFactory = new JsonConfigurationFactory();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("configuration.json")) {
      Configuration configuration = configurationFactory.load(in);
      Assert.assertEquals("The configuration bean was not read correctly.", expectedConfigurationBean, configuration);
    }
  }

}
