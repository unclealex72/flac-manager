package uk.co.unclealex.music.common.configuration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import uk.co.unclealex.music.common.Validator;
import uk.co.unclealex.music.common.configuration.Device;
import uk.co.unclealex.music.common.configuration.json.AmazonConfigurationBean;
import uk.co.unclealex.music.common.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.common.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.common.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.common.configuration.json.MtpDeviceBean;
import uk.co.unclealex.music.common.configuration.json.PathsBean;
import uk.co.unclealex.music.common.configuration.json.UserBean;
import uk.co.unclealex.music.violations.Violation;

import com.google.common.collect.Lists;

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

/**
 * @author alex
 * 
 */
public class ConfigurationValidationTest {

  PathsBean defaultPathBean = new PathsBean(Paths.get("a"), Paths.get("a"), Paths.get("a"), Paths.get("a"));
  AmazonConfigurationBean defaultAmazonBean = new AmazonConfigurationBean("endpoint", "accessKey", "secretKey");
  List<UserBean> defaultUsers = Lists.newArrayList(new UserBean("alex", "pwd", Lists
      .newArrayList((Device) new MtpDeviceBean("mtp"))));

  @Test
  public void testConfigurationRequiresPathAndUsers() throws Exception {
    testValidate(
        new ConfigurationBean(null, null, null),
        Violation.expect(NotNull.class, "directories"),
        Violation.expect(NotNull.class, "amazon"),
        Violation.expect(NotEmpty.class, "users"));
  }

  @Test
  public void testConfigurationRequiresAllPaths() throws Exception {
    testValidate(
        new ConfigurationBean(new PathsBean(null, null, null, null), defaultUsers, defaultAmazonBean),
        Violation.expect(NotNull.class, "directories", "stagingPath"),
        Violation.expect(NotNull.class, "directories", "encodedPath"),
        Violation.expect(NotNull.class, "directories", "flacPath"),
        Violation.expect(NotNull.class, "directories", "devicesPath"));
  }

  @Test
  public void testUserRequiresUserNamePasswordAndDevices() throws Exception {
    testValidate(
        new ConfigurationBean(defaultPathBean, Lists.newArrayList(new UserBean(null, null, null)), defaultAmazonBean),
        Violation.expect(NotEmpty.class, "users[0]", "userName"),
        Violation.expect(NotEmpty.class, "users[0]", "password"),
        Violation.expect(NotEmpty.class, "users[0]", "devices"));
  }

  @Test
  public void testAmazonRequiresUrlAndKeys() throws Exception {
    testValidate(
        new ConfigurationBean(defaultPathBean, defaultUsers, new AmazonConfigurationBean(null, null, null)),
        Violation.expect(NotEmpty.class, "amazon", "endpoint"),
        Violation.expect(NotEmpty.class, "amazon", "accessKey"),
        Violation.expect(NotEmpty.class, "amazon", "secretKey"));
  }

  @Test
  public void testDevices() throws Exception {
    testValidate(
        new ConfigurationBean(defaultPathBean, Lists.newArrayList(new UserBean("aj", "aj", Lists.newArrayList(
            (Device) new MtpDeviceBean(null),
            new FileSystemDeviceBean(null, null, null),
            new IpodDeviceBean(null)))), defaultAmazonBean),
        Violation.expect(NotEmpty.class, "users[0]", "devices[0]", "name"),
        Violation.expect(NotEmpty.class, "users[0]", "devices[1]", "name"),
        Violation.expect(NotNull.class, "users[0]", "devices[1]", "mountPoint"),
        Violation.expect(NotNull.class, "users[0]", "devices[2]", "mountPoint"));
  }

  public void testValidate(ConfigurationBean configurationBean, Violation... expectedViolations) {
    Validator validator = new Validator();
    Set<Violation> actualViolations = Violation.typedViolations(validator.generateViolations(configurationBean));
    assertThat("The wrong violations were found.", actualViolations, containsInAnyOrder(expectedViolations));
  }
}
