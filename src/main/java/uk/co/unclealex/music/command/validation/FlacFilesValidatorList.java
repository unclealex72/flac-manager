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

package uk.co.unclealex.music.command.validation;

import java.util.ArrayList;

/**
 * The default list of {@link FlacFilesValidator}s to use.
 * @author alex
 * 
 */
public class FlacFilesValidatorList extends ArrayList<FlacFilesValidator> {

  /**
   * 
   * @param findMissingCoverArtFlacFilesValidator
   *          The validator used to look for missing cover art.
   * @param uniqueFlacFilesValidator
   *          The validator used to check that all generated files are unique.
   * @param noOverwritingFlacFilesValidator
   *          The validator used to check that no existing files will be
   *          overwritten.
   * @param failuresOnlyFlacFilesValidator
   *          The validator used to make sure only failures are executed if at
   *          least one exists.
   */
  public FlacFilesValidatorList(
      @FindMissingCoverArt FlacFilesValidator findMissingCoverArtFlacFilesValidator,
      @Unique FlacFilesValidator uniqueFlacFilesValidator,
      @NoOverwriting FlacFilesValidator noOverwritingFlacFilesValidator,
      @FailuresOnly FlacFilesValidator failuresOnlyFlacFilesValidator) {
    super();
    add(findMissingCoverArtFlacFilesValidator);
    add(uniqueFlacFilesValidator);
    add(noOverwritingFlacFilesValidator);
    add(failuresOnlyFlacFilesValidator);
  }
}
