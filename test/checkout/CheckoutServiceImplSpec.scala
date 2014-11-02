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

package checkout

import java.nio.file.Paths

import common.configuration.Directories
import common.files.{StagedFlacFileLocation, FileUtils, FlacFileLocation}
import common.message.MessageService
import common.message._
import org.specs2.mock.Mockito
import org.specs2.mutable._

/**
 * Created by alex on 02/11/14.
 */
class CheckoutServiceImplSpec extends Specification with Mockito {

  implicit val directories: Directories =
    Directories(Paths.get("/flac"), Paths.get("/devices"), Paths.get("/encoded"), Paths.get("/staging"))

  "checking out a flac file" should {
    "log and move it to the staging directory" in {
      val flacFileLocation = FlacFileLocation("a", "b", "c.flac")
      val fileUtils = mock[FileUtils]
      implicit val messageService = mock[MessageService]
      val checkoutService = new CheckoutServiceImpl(fileUtils)
      val result = checkoutService.checkout(Seq(flacFileLocation))
      result must beASuccessfulTry
      val expectedStagedFlacFileLocation = StagedFlacFileLocation("a", "b", "c.flac")
      there was one(messageService).printMessage(MOVE(flacFileLocation, expectedStagedFlacFileLocation))
      there was one(fileUtils).move(flacFileLocation, expectedStagedFlacFileLocation)
    }
  }
}
