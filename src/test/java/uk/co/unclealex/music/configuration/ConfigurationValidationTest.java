package uk.co.unclealex.music.configuration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import uk.co.unclealex.music.JValidatorImpl;
import uk.co.unclealex.music.configuration.json.AmazonConfigurationBean;
import uk.co.unclealex.music.configuration.json.JConfigurationBean;
import uk.co.unclealex.music.configuration.json.JCowonX7DeviceBean;
import uk.co.unclealex.music.configuration.json.JFileSystemDeviceBean;
import uk.co.unclealex.music.configuration.json.JIpodDeviceBean;
import uk.co.unclealex.music.configuration.json.JPathsBean;
import uk.co.unclealex.music.configuration.json.JUserBean;
import uk.co.unclealex.music.violations.Violation;
import uk.co.unclealex.validator.paths.CanRead;
import uk.co.unclealex.validator.paths.CanWrite;
import uk.co.unclealex.validator.paths.IsDirectory;

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

  Path homeDir = Paths.get(System.getProperty("user.home"));
  JPathsBean defaultPathBean = new JPathsBean(homeDir, homeDir, homeDir, homeDir);
  AmazonConfigurationBean defaultAmazonBean = new AmazonConfigurationBean("endpoint", "accessKey", "secretKey");
  List<JUserBean> defaultUsers = Lists.newArrayList(new JUserBean("alex", "MeMeMe", "pwd", Lists
      .newArrayList((JDevice) new JFileSystemDeviceBean("WALKMAN", "123456", Paths.get("Music")))));

  @Test
  public void testConfigurationRequiresPathAndUsers() throws Exception {
    testValidate(
        new JConfigurationBean(null, null, null),
        Violation.expect(NotNull.class, "directories"),
        Violation.expect(NotNull.class, "amazon"),
        Violation.expect(NotEmpty.class, "users"));
  }

  @Test
  public void testConfigurationRequiresAllPaths() throws Exception {
    testValidate(
        new JConfigurationBean(new JPathsBean(null, null, null, null), defaultUsers, defaultAmazonBean),
        Violation.expect(NotNull.class, "directories", "stagingPath"),
        Violation.expect(NotNull.class, "directories", "encodedPath"),
        Violation.expect(NotNull.class, "directories", "flacPath"),
        Violation.expect(NotNull.class, "directories", "devicesPath"));
  }

  @Test
  public void testConfigurationRequiresValidPaths() throws Exception {
    testValidate(new JConfigurationBean(
        new JPathsBean(Paths.get("a"), Paths.get("a"), Paths.get("a"), Paths.get("a")),
        defaultUsers,
        defaultAmazonBean), Violation.expect(IsDirectory.class, "directories", "stagingPath"), Violation.expect(
        CanRead.class,
        "directories",
        "stagingPath"), Violation.expect(CanWrite.class, "directories", "stagingPath"), Violation.expect(
        IsDirectory.class,
        "directories",
        "encodedPath"), Violation.expect(CanRead.class, "directories", "encodedPath"), Violation.expect(
        IsDirectory.class,
        "directories",
        "flacPath"), Violation.expect(CanRead.class, "directories", "flacPath"), Violation.expect(
        IsDirectory.class,
        "directories",
        "devicesPath"), Violation.expect(CanRead.class, "directories", "devicesPath"));
  }

  @Test
  public void testUserRequiresUserNamePasswordAndDevices() throws Exception {
    testValidate(new JConfigurationBean(
        defaultPathBean,
        Lists.newArrayList(new JUserBean(null, null, null, null)),
        defaultAmazonBean), Violation.expect(NotEmpty.class, "users[0]", "name"), Violation.expect(
        NotEmpty.class,
        "users[0]",
        "musicBrainzUserName"), Violation.expect(NotEmpty.class, "users[0]", "musicBrainzPassword"), Violation.expect(
        NotEmpty.class,
        "users[0]",
        "devices"));
  }

  @Test
  public void testAmazonRequiresUrlAndKeys() throws Exception {
    testValidate(
        new JConfigurationBean(defaultPathBean, defaultUsers, new AmazonConfigurationBean(null, null, null)),
        Violation.expect(NotEmpty.class, "amazon", "endpoint"),
        Violation.expect(NotEmpty.class, "amazon", "accessKey"),
        Violation.expect(NotEmpty.class, "amazon", "secretKey"));
  }

  @Test
  public void testDevices() throws Exception {
    testValidate(
        new JConfigurationBean(defaultPathBean, Lists.newArrayList(new JUserBean("aj", "aj", "aj", Lists.newArrayList(
            (JDevice) new JFileSystemDeviceBean(null, null, null),
            new JCowonX7DeviceBean(null),
            new JIpodDeviceBean(null)))), defaultAmazonBean),
        Violation.expect(NotEmpty.class, "users[0]", "devices[0]", "name"),
        Violation.expect(NotEmpty.class, "users[0]", "devices[0]", "uuid"),
        Violation.expect(NotEmpty.class, "users[0]", "devices[1]", "uuid"),
        Violation.expect(NotEmpty.class, "users[0]", "devices[2]", "uuid"));
  }

  public void testValidate(final JConfigurationBean configurationBean, final Violation... expectedViolations) {
    final JValidatorImpl validator = new JValidatorImpl();
    final Set<Violation> actualViolations = Violation.typedViolations(validator.generateViolations(configurationBean));
    assertThat("The wrong violations were found.", actualViolations, containsInAnyOrder(expectedViolations));
  }
}
