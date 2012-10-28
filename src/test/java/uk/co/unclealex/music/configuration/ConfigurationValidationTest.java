package uk.co.unclealex.music.configuration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import uk.co.unclealex.music.common.Validator;
import uk.co.unclealex.music.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.configuration.json.FileSystemDeviceBean;
import uk.co.unclealex.music.configuration.json.IpodDeviceBean;
import uk.co.unclealex.music.configuration.json.MtpDeviceBean;
import uk.co.unclealex.music.configuration.json.PathsBean;
import uk.co.unclealex.music.configuration.json.UserBean;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
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

  @NotNull
  @NotEmpty
  public String dummy;

  PathsBean defaultPathBean = new PathsBean(Paths.get("a"), Paths.get("a"), Paths.get("a"), Paths.get("a"));

  @Test
  public void testConfigurationRequiresPathAndUsers() throws Exception {
    testValidate(
        new ConfigurationBean(null, null),
        expectViolation(NotNull.class, "directories"),
        expectViolation(NotEmpty.class, "users"));
  }

  @Test
  public void testConfigurationRequiresAllPaths() throws Exception {
    testValidate(
        new ConfigurationBean(new PathsBean(null, null, null, null), Lists.newArrayList(new UserBean(
            "alex",
            "pwd",
            Lists.newArrayList((Device) new MtpDeviceBean("mtp"))))),
        expectViolation(NotNull.class, "directories", "stagingPath"),
        expectViolation(NotNull.class, "directories", "encodedPath"),
        expectViolation(NotNull.class, "directories", "flacPath"),
        expectViolation(NotNull.class, "directories", "devicesPath"));
  }

  @Test
  public void testUserRequiresUserNamePasswordAndDevices() throws Exception {
    testValidate(
        new ConfigurationBean(defaultPathBean, Lists.newArrayList(new UserBean(null, null, null))),
        expectViolation(NotNull.class, "users[0]", "userName"),
        expectViolation(NotNull.class, "users[0]", "password"),
        expectViolation(NotEmpty.class, "users[0]", "devices"));
  }

  @Test
  public void testDevices() throws Exception {
    testValidate(
        new ConfigurationBean(defaultPathBean, Lists.newArrayList(new UserBean("aj", "aj", Lists
            .newArrayList((Device) new MtpDeviceBean(null), new FileSystemDeviceBean(null, null, null), 
                new IpodDeviceBean(null))))),
        expectViolation(NotNull.class, "users[0]", "devices[0]", "name"),
        expectViolation(NotNull.class, "users[0]", "devices[1]", "name"),
        expectViolation(NotNull.class, "users[0]", "devices[1]", "mountPoint"),
        expectViolation(NotNull.class, "users[0]", "devices[2]", "mountPoint")
        );
  }

  public void testValidate(ConfigurationBean configurationBean, Violation... expectedViolations) {
    Validator validator = new Validator();
    List<Violation> actualViolations =
        Lists
            .newArrayList(Iterables.transform(validator.generateViolations(configurationBean), new ViolationFactory()));
    assertThat("The wrong violations were found.", actualViolations, containsInAnyOrder(expectedViolations));
  }

  public Violation expectViolation(Class<? extends Annotation> expectedAnnotation, String... propertyPath)
      throws Exception {
    Annotation annotation = ConfigurationValidationTest.class.getField("dummy").getAnnotation(expectedAnnotation);
    String messageTemplate = (String) annotation.getClass().getMethod("message").invoke(annotation);
    return Violation.expected(messageTemplate, propertyPath);
  }

  class ViolationFactory implements Function<ConstraintViolation<ConfigurationBean>, Violation> {

    public Violation apply(ConstraintViolation<ConfigurationBean> constraintViolation) {
      return Violation.actual(constraintViolation.getMessageTemplate(), constraintViolation
          .getPropertyPath()
          .toString());
    }
  }

  static class Violation {

    static Violation expected(String messageTemplate, String... propertyPath) {
      return new Violation(Joiner.on('.').join(propertyPath), messageTemplate);
    }

    static Violation actual(String messageTemplate, String propertyPath) {
      return new Violation(propertyPath, messageTemplate);
    }

    /**
     * The path of the property.
     */
    private final String propertyPath;

    /**
     * The generated message template.
     */
    private final String messageTemplate;

    public Violation(String propertyPath, String messageTemplate) {
      super();
      this.propertyPath = propertyPath;
      this.messageTemplate = messageTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    public String getPropertyPath() {
      return propertyPath;
    }

    public String getMessageTemplate() {
      return messageTemplate;
    }
  }
}
