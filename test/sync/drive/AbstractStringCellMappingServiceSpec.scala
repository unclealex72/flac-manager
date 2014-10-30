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

package sync.drive

import java.io.IOException

import org.specs2.mutable._

/**
 * @author alex
 *
 */
class AbstractStringCellMappingServiceSpec extends Specification {

  "The abstract string cell mapping service" should {
    "parse a sequence of lines successfully" in {
      val cellMappingService = new AbstractStringCellMappingService[String, String](1, -3) {

        override def parseKey(key: String): Option[String] = if (!key.contains("twenty")) Some(s"Key: $key") else None

        override def parseValue(value: String): Option[String] = if (!value.contains("thirty")) Some(s"Value: $value") else None

        override def generateLines: Traversable[String] =
          Seq(
            "one    two   three four five six seven    eight",
            "ten  eleven   twelve    thirteen  fourteen   fifteen",
            "twenty    twenty-one   twenty-two twenty-three twenty-four twenty-five",
            "thirty   thirty-one thirty-two thirty-three    thirty-four thirty-five")
      }
      cellMappingService.generateMap must contain(exactly("Key: two" -> "Value: six", "Key: eleven" -> "Value: thirteen"))
    }
  }
}