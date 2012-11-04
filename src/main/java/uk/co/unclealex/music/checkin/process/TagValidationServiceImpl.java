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

package uk.co.unclealex.music.checkin.process;

import java.nio.file.Path;
import java.util.Set;
import java.util.SortedMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import uk.co.unclealex.music.common.MusicFile;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * The default implementation of {@link TagValidationService}.
 * 
 * @author alex
 * 
 */
public class TagValidationServiceImpl implements TagValidationService {

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedMap<Path, Set<ConstraintViolation<MusicFile>>> checkTags(
      SortedMap<Path, MusicFile> musicFilesByPath) {
    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Function<MusicFile, Set<ConstraintViolation<MusicFile>>> constraintFunction =
        new Function<MusicFile, Set<ConstraintViolation<MusicFile>>>() {
          public Set<ConstraintViolation<MusicFile>> apply(MusicFile musicFile) {
            return validator.validate(musicFile);
          }
        };
    Predicate<Set<ConstraintViolation<MusicFile>>> isNotEmptyPredicate =
        new Predicate<Set<ConstraintViolation<MusicFile>>>() {
          public boolean apply(Set<ConstraintViolation<MusicFile>> constraintViolations) {
            return !constraintViolations.isEmpty();
          }
        };
    return Maps.filterValues(Maps.transformValues(musicFilesByPath, constraintFunction), isNotEmptyPredicate);
  }

}
