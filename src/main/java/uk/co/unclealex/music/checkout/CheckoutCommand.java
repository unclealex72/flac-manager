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

package uk.co.unclealex.music.checkout;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Unparsed;

import uk.co.unclealex.executable.Executable;
import uk.co.unclealex.music.common.command.Command;
import uk.co.unclealex.music.common.command.CommandLine;
import uk.co.unclealex.music.common.command.Execution;
import uk.co.unclealex.music.common.exception.InvalidDirectoriesException;

/**
 * @author alex
 *
 */
public class CheckoutCommand extends Command<CheckoutCommandLine> {

  @Inject
  public CheckoutCommand(Execution execution) {
    super(execution);
  }

  @Override
  @Executable(CheckoutModule.class)
  public void execute(CheckoutCommandLine commandLine) throws IOException, InvalidDirectoriesException {
    super.execute(commandLine);
  }
}

@CommandLineInterface(application="flacman-checkout")
interface CheckoutCommandLine extends CommandLine {
  
  @Unparsed(minimum = 1, name = "flac-directories", description = "A list of flac directories to check out.")
  public List<String> getFlacFiles();
}