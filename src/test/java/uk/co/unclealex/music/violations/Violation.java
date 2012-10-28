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

package uk.co.unclealex.music.violations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
public class Violation {

  static class AnnotationHolder {
    @NotNull
    @NotEmpty
    public transient String dummy;
  }

  public static Violation expect(Class<? extends Annotation> expectedAnnotation, String... expectedPropertyPath) {
    Annotation annotation;
    try {
      annotation = AnnotationHolder.class.getField("dummy").getAnnotation(expectedAnnotation);
      String messageTemplate = (String) annotation.getClass().getMethod("message").invoke(annotation);
      return new Violation(messageTemplate, Joiner.on('.').join(expectedPropertyPath));
    }
    catch (
        NoSuchFieldException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new IllegalStateException("Could not get a validation message.", e);
    }
  }

  public static Violation actual(ConstraintViolation<?> constraintViolation) {
    return new Violation(constraintViolation.getMessageTemplate(), constraintViolation.getPropertyPath().toString());
  }

  public static <T> Set<Violation> typedViolations(Set<ConstraintViolation<T>> constraintViolations) {
    Set<Violation> violations = Sets.newHashSet();
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {
      violations.add(actual(constraintViolation));
    }
    return violations;
  }

  public static Set<Violation> untypedViolations(Set<ConstraintViolation<?>> constraintViolations) {
    Set<Violation> violations = Sets.newHashSet();
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {
      violations.add(actual(constraintViolation));
    }
    return violations;
  }

  /**
   * The path of the property.
   */
  private final String propertyPath;

  /**
   * The generated message template.
   */
  private final String messageTemplate;

  public Violation(String messageTemplate, String propertyPath) {
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
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
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