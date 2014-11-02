/*
 * Copyright 2014 Alex Jones
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
 */

package common.commands

import org.specs2.mutable.Specification

import scala.sys.process._
import ProcessCommunicator._

/**
 * Created by alex on 29/10/14.
 */
class ProcessCommunicatorSpec extends Specification {
  sequential

  "The process communicator" should {
    "be able to read output from an echoing command" in {
      val commandService = new TempFileCommandService
      val echoCommand = commandService.create("doubleecho.py")
      val processCommunicator = new ProcessCommunicator
      echoCommand run processCommunicator
      processCommunicator.write("Hello")
      val result = processCommunicator.read
      processCommunicator.write("QUIT")
      result must contain(exactly("HelloA", "HelloB"))
    }
  }
}
