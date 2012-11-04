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

package uk.co.unclealex.music.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import uk.co.unclealex.music.files.FileLocation;

/**
 * An action used to donate a FLAC file was not tagged correctly.
 * 
 * @author alex
 * 
 */
public class FailureAction extends AbstractAction implements Action {

  /**
   * The message template used to display to the user.
   */
  private final String messageTemplate;
  
  /**
   * A list of parameters to add to the message template.
   */
  private final List<Object> parameters;
  
  
  /**
   * Instantiates a new failure action.
   *
   * @param fileLocation
   * @param messageTemplate the message template
   * @param parameters the parameters
   */
  public FailureAction(FileLocation fileLocation, String messageTemplate, Object... parameters) {
    super(fileLocation);
    this.messageTemplate = messageTemplate;
    this.parameters = Arrays.asList(parameters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ActionVisitor actionVisitor) throws IOException {
    actionVisitor.visit(this);
  }

  /**
   * Gets the message template used to display to the user.
   *
   * @return the message template used to display to the user
   */
  public String getMessageTemplate() {
    return messageTemplate;
  }

  /**
   * Gets the a list of parameters to add to the message template.
   *
   * @return the a list of parameters to add to the message template
   */
  public List<Object> getParameters() {
    return parameters;
  }
}
