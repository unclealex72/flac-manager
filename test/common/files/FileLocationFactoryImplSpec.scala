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
 * http://www.apache.org/licenses/LICENSE-2.0
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

package files

import java.nio.file.Paths
import common.configuration.Directories
import common.files.{FileLocationFactoryImpl, FileLocation}
import org.specs2.mutable._

/**
 * @author alex
 *
 */
class FileLocationFactoryImplSpec extends Specification {

  val directories = Directories(
    Paths.get("/flacPath"),
    Paths.get("/devicesPath"),
    Paths.get("/encodedPath"),
    Paths.get("/stagingPath"));

  val fileLocationFactory = new FileLocationFactoryImpl(directories)

  "creating a flac file should" should {
    "create a file in the flac directory" in {
      fileLocationFactory.createFlacFileLocation(Paths.get("my", "flac", "file.flac")) must be equalTo (
        FileLocation(Paths.get("/flacPath"), Paths.get("my", "flac", "file.flac"), true)
        )
    }
  }

  "creating an encoded file" should {
    "create a file in the encoded directory" in {
      fileLocationFactory.createEncodedFileLocation(Paths.get("my", "encoded", "file.mp3")) must be equalTo (
        FileLocation(Paths.get("/encodedPath"), Paths.get("my", "encoded", "file.mp3"), true)
        )
    }
  }

  "creating a staging file" should {
    "create a file in the staging directory" in {
      fileLocationFactory.createStagingFileLocation(Paths.get("my", "staging", "file.flac")) must be equalTo (
        FileLocation(Paths.get("/stagingPath"), Paths.get("my", "staging", "file.flac"), false)
        )
    }
  }
}
