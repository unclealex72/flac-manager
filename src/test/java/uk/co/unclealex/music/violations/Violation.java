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

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;

import uk.co.unclealex.music.common.DataObject;
import uk.co.unclealex.validator.paths.CanRead;
import uk.co.unclealex.validator.paths.CanWrite;
import uk.co.unclealex.validator.paths.IsDirectory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * The Class Violation.
 *
 * @author alex
 */
public class Violation extends DataObject {

  /**
   * The Class AnnotationHolder.
   */
  static class AnnotationHolder {
    
    /** The dummy. */
    @NotNull
    @NotEmpty
    @CanRead
    @CanWrite
    @IsDirectory
    public transient String dummy;
  }

  /**
   * Expect.
   *
   * @param expectedAnnotation the expected annotation
   * @param expectedPropertyPath the expected property path
   * @return the violation
   */
  public static Violation expect(Class<? extends Annotation> expectedAnnotation, String... expectedPropertyPath) {
    Annotation annotation;
    try {
      annotation = AnnotationHolder.class.getField("dummy").getAnnotation(expectedAnnotation);
      if (annotation == null) {
        Assert.fail("The annotation holder requires annotation " + expectedAnnotation + " to be declared.");
      }
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

  /**
   * Actual.
   *
   * @param constraintViolation the constraint violation
   * @return the violation
   */
  public static Violation actual(ConstraintViolation<?> constraintViolation) {
    return new Violation(constraintViolation.getMessageTemplate(), constraintViolation.getPropertyPath().toString());
  }

  /**
   * Typed violations.
   *
   * @param <T> the generic type
   * @param constraintViolations the constraint violations
   * @return the sets the
   */
  public static <T> Set<Violation> typedViolations(Set<ConstraintViolation<T>> constraintViolations) {
    Set<Violation> violations = Sets.newHashSet();
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {
      violations.add(actual(constraintViolation));
    }
    return violations;
  }

  /**
   * Untyped violations.
   *
   * @param constraintViolations the constraint violations
   * @return the sets the
   */
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

  /**
   * Instantiates a new violation.
   *
   * @param messageTemplate the message template
   * @param propertyPath the property path
   */
  public Violation(String messageTemplate, String propertyPath) {
    super();
    this.propertyPath = propertyPath;
    this.messageTemplate = messageTemplate;
  }

  /**
   * Gets the path of the property.
   *
   * @return the path of the property
   */
  public String getPropertyPath() {
    return propertyPath;
  }

  /**
   * Gets the generated message template.
   *
   * @return the generated message template
   */
  public String getMessageTemplate() {
    return messageTemplate;
  }
}