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

package commands

import java.nio.file.{Files, Paths}

import org.specs2.mutable._

import scala.io.Source

/**
 * Created by alex on 27/10/14.
 */
class TempFileCommandServiceSpec extends Specification {

  val commandService = new TempFileCommandService()

  "The temporary file command service" should {
    Seq("flac2mp3.sh" -> commandService.flac2mp3Command, "sync.py" -> commandService.syncCommand).foreach { case (commandName, commandFile) =>
      val commandPath = Paths.get(commandFile)
      s"create a $commandName file that" in {
        "exists" in {
          Files.exists(commandPath) must beTrue
        }
        "is a regular file" in {
          Files.isRegularFile(commandPath) must beTrue
        }
        "is readable" in {
          Files.isReadable(commandPath) must beTrue
        }
        "is executable" in {
          Files.isExecutable(commandPath) must beTrue
        }
        s"has contents identical to the resource $commandName" in {
          val actualContents = Source.fromFile(commandPath.toFile).mkString
          val expectedContents = Source.fromURL(getClass.getResource(commandName)).mkString
          actualContents must be equalTo (expectedContents)
        }
      }
    }
  }

}
