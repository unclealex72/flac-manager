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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * An interface for validitating an object and throwing a JSR-303
 * {@link ConstraintViolationException} if it is not.
 * 
 * @author alex
 * 
 */
public interface JValidator {

  /**
   * Validate an object.
   * 
   * @param object
   *          The object to validate.
   * @return The object that was validated.
   * @throws ConstraintViolationException
   *           Thrown if any constraints are violated.
   */
  public <T> T validate(T object, String message);

  /**
   * Validate an object.
   * 
   * @param object
   *          The object to validate.
   * @return A set of {@link ConstraintViolation}s.
   */
  public <T> Set<ConstraintViolation<T>> generateViolations(T object);

}