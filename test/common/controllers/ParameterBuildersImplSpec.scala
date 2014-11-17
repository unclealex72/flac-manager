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

package common.controllers

import java.nio.file.Paths

import common.configuration.{Directories, User, Users}
import common.files._
import controllers._
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope
import play.api.data.FormError
import play.api.test.FakeRequest

/**
 * Created by alex on 09/11/14.
 */
class ParameterBuildersImplSpec extends Specification with Mockito {

  trait Context extends Scope {
    lazy implicit val directories = Directories(Paths.get("/flac"), Paths.get("/devices"), Paths.get("/encoded"), Paths.get("/staging"), Paths.get("/temp"))
    val MTAB = "mtab" -> "some"
    lazy implicit val fileLocationUtils = mock[TestFileLocationExtensions]
    fileLocationUtils.isDirectory(any[FileLocation]) answers { fileLocation =>
      fileLocation.asInstanceOf[FileLocation].relativePath.getFileName.toString == "dir"
    }
    lazy val users = mock[Users]
    val brian: User = User("Brian", "", "", "")
    val freddie: User = User("Freddie", "", "", "")
    users.allUsers returns (Seq(brian, freddie))

    lazy val directoryMappingService = mock[DirectoryMappingService]
    directoryMappingService.withMtab(MTAB._2) answers (_ => path => Paths.get(path))
    lazy val parameterBuilders = new ParameterBuildersImpl(users, directoryMappingService)

    def bind[P](parameterBuilder: ParameterBuilder[P], params: (String, Any)*): Either[Seq[FormError], P] = {
      val request = FakeRequest().withFormUrlEncodedBody(params.map { case (k, v) => (k, v.toString)}: _*)
      parameterBuilder.bindFromRequest()(request)
    }
  }

  "The sync form" should {
    "not require any parameters" in new Context {
      val result = bind(parameterBuilders.syncParametersBuilder)
      result must beRight(SyncParameters)
    }
  }

  "The checkin form" should {
    "require at least one directory" in new Context {
      val result = bind(parameterBuilders.checkinParametersBuilder, MTAB)
      result must beLeft(contain(beAFormError("directories", "error.min")).exactly(1))
    }
    "require an mtab" in new Context {
      val result = bind(parameterBuilders.checkinParametersBuilder, "directories[0]" -> directories.stagingPath.resolve("dir"))
      result must beLeft(contain(beAFormError("mtab", "error.required")).exactly(1))
    }
    "reject non-staging directories" in new Context {
      val result = bind(parameterBuilders.checkinParametersBuilder, MTAB, "directories[0]" -> directories.flacPath.resolve("dir"))
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/flac/dir")).exactly(1))
    }
    "reject staging files" in new Context {
      val result = bind(parameterBuilders.checkinParametersBuilder, MTAB, "directories[0]" -> directories.stagingPath.resolve("file"))
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/staging/file")).exactly(1))
    }
    "accept staging directories" in new Context {
      val result = bind(parameterBuilders.checkinParametersBuilder, MTAB, "directories[0]" -> directories.stagingPath.resolve("dir"))
      result must beRight(CheckinParameters(Seq(StagedFlacFileLocation("dir"))))
    }
  }

  "The checkout form" should {
    "require at least one directory" in new Context {
      val result = bind(parameterBuilders.checkoutParametersBuilder, MTAB)
      result must beLeft(contain(beAFormError("directories", "error.min")).exactly(1))
    }
    "require an mtab" in new Context {
      val result = bind(parameterBuilders.checkoutParametersBuilder, "directories[0]" -> directories.flacPath.resolve("dir"))
      result must beLeft(contain(beAFormError("mtab", "error.required")).exactly(1))
    }
    "reject non-repository directories" in new Context {
      val result = bind(parameterBuilders.checkoutParametersBuilder, MTAB, "directories[0]" -> directories.stagingPath.resolve("dir"))
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/staging/dir")).exactly(1))
    }
    "reject repository files" in new Context {
      val result = bind(parameterBuilders.checkoutParametersBuilder, MTAB, "directories[0]" -> directories.flacPath.resolve("file"))
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/flac/file")).exactly(1))
    }
    "accept repository directories" in new Context {
      val result = bind(parameterBuilders.checkoutParametersBuilder, MTAB, "directories[0]" -> directories.flacPath.resolve("dir"))
      result must beRight(CheckoutParameters(Seq(FlacFileLocation("dir"))))
    }
  }

  "The owner form" should {
    "require at least one directory" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder, MTAB, "users[0]" -> brian.name)
      result must beLeft(contain(beAFormError("directories", "error.min")).exactly(1))
    }
    "require an mtab" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder, "directories[0]" -> directories.stagingPath.resolve("dir"), "users[0]" -> brian.name)
      result must beLeft(contain(beAFormError("mtab", "error.required")).exactly(1))
    }
    "require a user" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder, MTAB, "directories[0]" -> directories.stagingPath.resolve("dir"))
      result must beLeft(contain(beAFormError("users", "error.min")).exactly(1))
    }
    "reject non-staging directories" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder,
        MTAB, "directories[0]" -> directories.flacPath.resolve("dir"), "users[0]" -> brian.name)
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/flac/dir")).exactly(1))
    }
    "reject staging files" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder,
        MTAB, "directories[0]" -> directories.stagingPath.resolve("file"), "users[0]" -> brian.name)
      result must beLeft(contain(beAFormErrorWithArgs("directories[0]", "invalidPath", "/staging/file")).exactly(1))
    }
    "accept staging directories" in new Context {
      val result = bind(parameterBuilders.ownerParametersBuilder,
        MTAB, "directories[0]" -> directories.stagingPath.resolve("dir"), "users[0]" -> brian.name, "users[1]" -> freddie.name)
      result must beRight(OwnerParameters(Seq(StagedFlacFileLocation("dir")), Seq(brian, freddie)))
    }
  }

  def beAFormError(key: String, message: String): Matcher[FormError] = {
    ((fe: FormError) => fe.key must beEqualTo(key)) and
      ((fe: FormError) => fe.message must beEqualTo(message))
  }

  def beAFormErrorWithArgs(key: String, message: String, args: Any*): Matcher[FormError] = {
    beAFormError(key, message) and ((fe: FormError) => fe.args must containTheSameElementsAs(args))
  }
}
