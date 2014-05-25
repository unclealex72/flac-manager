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

package uk.co.unclealex.music;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * A class that uses a JSR-303 {@link JValidator} to make sure that objects are valid.
 */
public class JValidatorImpl implements JValidator {

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T validate(T object, String message) {
    Set<ConstraintViolation<T>> constraintViolations = generateViolations(object);
    if (!constraintViolations.isEmpty()) {
      throw new ConstraintViolationException(message, new HashSet<ConstraintViolation<?>>(constraintViolations));
    }
    return object;
  }

  @Override
  public <T> Set<ConstraintViolation<T>> generateViolations(T object) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    javax.validation.Validator validator = factory.getValidator();
    return validator.validate(object);
  }
}
