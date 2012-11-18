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

package uk.co.unclealex.music.command.own;

import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.command.OwnCommand;
import uk.co.unclealex.music.command.OwnCommandLine;
import uk.co.unclealex.music.command.inject.CommonModule;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * The {@link Module} for the {@link OwnCommand}.
 * @author alex
 *
 */
public class OwnModule extends CommonModule<OwnCommandLine, OwnExecution> {

  public OwnModule() {
    super(OwnExecution.class, new TypeLiteral<Execution<OwnCommandLine>>() {});
  }
}