/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.commands

import common.commands.ProcessCommunicator._
import org.specs2.mutable._

import scala.sys.process._

/**
 * Created by alex on 29/10/14.
 */
class ProcessCommunicatorSpec extends Specification {
  sequential

  trait Context extends After {
    lazy val processCommunicator = new ProcessCommunicator

    def after: Unit = processCommunicator.close()
  }

  "The process communicator" should {
    "be able to read output from an echoing command" in pending("Occasionally hangs") { new Context() {
      val commandService = new TempFileCommandService
      val echoCommand = commandService.create("doubleecho.py")
      echoCommand run processCommunicator
      processCommunicator.write("Hello")
      val result = processCommunicator.read
      println(result)
      processCommunicator.write("QUIT")
      result must contain(exactly("HelloA", "HelloB"))
    }
  }}
}
