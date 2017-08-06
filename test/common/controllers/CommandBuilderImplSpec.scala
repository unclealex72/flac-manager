/*
 * Copyright 2017 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.controllers

import java.nio.file.FileSystem
import java.util.concurrent.TimeUnit

import calibrate.CalibrateCommand
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import checkin.CheckinCommand
import checkout.CheckoutCommand
import com.typesafe.scalalogging.StrictLogging
import common.configuration.{User, UserDao}
import common.files.Directory.{FlacDirectory, StagingDirectory}
import common.files.{Repositories, RepositoryEntry, TestRepositories}
import common.message.Messages._
import common.message.{Message, MessageService}
import controllers.CommandBuilderImpl
import initialise.InitialiseCommand
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}
import json._
import multidisc.MultiDiscCommand
import org.specs2.matcher.MatchResult
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import own.{OwnAction, OwnCommand}
import play.api.libs.json._

import scala.collection.SortedSet
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import common.files.RepositoryEntry

/**
  * Created by alex on 23/04/17
  **/
class CommandBuilderImplSpec extends Specification with StrictLogging with TestRepositories[(FileSystem, Repositories)] with Mockito with RepositoryEntry.Dsl {

  val entries: Seq[FsEntryBuilder] = Repos(
    staging = Seq(D("A"), D("B")),
    flac = Artists(
      "Queen" -> Albums(Album("A Night at the Opera", Tracks("Death on Two Legs"))),
      "Slayer" -> Albums(Album("Reign in Blood", Tracks("Angel of Death")))
    )
  )

  "running the initialise command" should {
    "only require a command to call successfully" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(Seq("command" -> JsString("initialise")))
      }
      validatedResult must beRight { (c: Commands) =>
        there was one(c.initialiseCommand).initialiseDb
        c.noMore()
      }
    }
  }

  "running the checkin command" should {
    "require directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "allowUnowned" -> JsBoolean(false),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NO_DIRECTORIES("staging")))
      }
    }
    "not allow flac directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "allowUnowned" -> JsBoolean(false),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NOT_A_DIRECTORY(fs.getPath("Q"), "staging")))
      }
    }
    "pass on its parameters" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkin"),
            "allowUnowned" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(stagingObj("A"), stagingObj("B")))))
      }
      validatedResult must beRight { (c: Commands) =>
        repositories.staging.directory(fs.getPath("A")).toEither must beRight { a: StagingDirectory =>
          repositories.staging.directory(fs.getPath("B")).toEither must beRight { b: StagingDirectory =>
            there was one(c.checkinCommand).checkin(SortedSet(a, b), allowUnowned = true)
            c.noMore()
          }
        }
      }
    }
  }

  "running the multi command" should {
    "require directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("multidisc"),
            "action" -> JsString("join"),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NO_DIRECTORIES("staging")))
      }
    }
    "not allow flac directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("multidisc"),
            "action" -> JsString("join"),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NOT_A_DIRECTORY(fs.getPath("Q"), "staging")))
      }
    }
    "require an action" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("multidisc"),
            "relativeDirectories" -> JsArray(Seq(stagingObj("A"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(MULTI_ACTION_REQUIRED))
      }
    }

    "pass on its parameters" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedJoinResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("multidisc"),
            "action" -> JsString("join"),
            "relativeDirectories" -> JsArray(Seq(stagingObj("A"), stagingObj("B")))))
      }
      validatedJoinResult must beRight { (c: Commands) =>
        repositories.staging.directory(fs.getPath("A")).toEither must beRight { a: StagingDirectory =>
          repositories.staging.directory(fs.getPath("B")).toEither must beRight { b: StagingDirectory =>
            there was one(c.multiDiscCommand).mutateMultiDiscAlbum(SortedSet(a, b), MultiAction.Join)
            c.noMore()
          }
        }
      }
      val validatedSplitResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("multidisc"),
            "action" -> JsString("split"),
            "relativeDirectories" -> JsArray(Seq(stagingObj("A"), stagingObj("B")))))
      }
      validatedSplitResult must beRight { (c: Commands) =>
        repositories.staging.directory(fs.getPath("A")).toEither must beRight { a: StagingDirectory =>
          repositories.staging.directory(fs.getPath("B")).toEither must beRight { b: StagingDirectory =>
            there was one(c.multiDiscCommand).mutateMultiDiscAlbum(SortedSet(a, b), MultiAction.Split)
            c.noMore()
          }
        }
      }
    }
  }

  "running the checkout command" should {
    "require directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NO_DIRECTORIES("flac")))
      }
    }
    "not allow staging directories" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), stagingObj("B")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NOT_A_DIRECTORY(fs.getPath("B"), "flac")))
      }
    }
    "pass on its parameters" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("checkout"),
            "unown" -> JsBoolean(true),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), flacObj("S")))))
      }
      validatedResult must beRight { (c: Commands) =>
        repositories.flac.directory(fs.getPath("Q")).toEither must beRight { a: FlacDirectory =>
          repositories.flac.directory(fs.getPath("S")).toEither must beRight { b: FlacDirectory =>
            there was one(c.checkoutCommand).checkout(SortedSet(a, b), unown = true)
            c.noMore()
          }
        }
      }
    }
  }

  "running the own command" should {
    "require directories and users" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NO_DIRECTORIES("staging or flac"), NO_USERS))
      }
    }
    "require valid users" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(INVALID_USER("Roger")))
      }
    }
    "pass on its parameters" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("own"),
            "users" -> JsArray(Seq(JsString("Brian"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), stagingObj("B")))))
      }
      validatedResult must beRight { (c: Commands) =>
        repositories.flac.directory(fs.getPath("Q")).toEither must beRight { a: FlacDirectory =>
          repositories.staging.directory(fs.getPath("B")).toEither must beRight { b: StagingDirectory =>
            there was one(c.ownCommand).changeOwnership(OwnAction.Own, SortedSet(User("Brian")), SortedSet(Right(a), Left(b)))
            c.noMore()
          }
        }
      }
    }
  }

  "running the unown command" should {
    "require directories and users" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(),
            "relativeDirectories" -> JsArray()))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(NO_DIRECTORIES("staging or flac"), NO_USERS))
      }
    }
    "require valid users" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(Seq(JsString("Roger"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q")))))
      }
      validatedResult must beLeft { (es: NonEmptyList[Message]) =>
        es.toList must containTheSameElementsAs(Seq(INVALID_USER("Roger")))
      }
    }
    "pass on its parameters" in { fsr: (FileSystem, Repositories) =>
      implicit val (fs, repositories) = fsr
      val validatedResult = execution {
        JsObject(
          Seq(
            "command" -> JsString("unown"),
            "users" -> JsArray(Seq(JsString("Brian"))),
            "relativeDirectories" -> JsArray(Seq(flacObj("Q"), stagingObj("B")))))
      }
      validatedResult must beRight { (c: Commands) =>
        repositories.flac.directory(fs.getPath("Q")).toEither must beRight { a: FlacDirectory =>
          repositories.staging.directory(fs.getPath("B")).toEither must beRight { b: StagingDirectory =>
            there was one(c.ownCommand).changeOwnership(OwnAction.Unown, SortedSet(User("Brian")), SortedSet(Right(a), Left(b)))
            c.noMore()
          }
        }
      }
    }
  }

  override def generate(fs: FileSystem, repositories: Repositories): (FileSystem, Repositories) = {
    fs.add(entries :_*)
    (fs, repositories)
  }

  def execution(jsValue: JsValue)(implicit repositories: Repositories, fs: FileSystem): Either[NonEmptyList[Message], Commands] = {
    val userDao = new UserDao {
      override def allUsers(): Set[User] = Set("Freddie", "Brian").map(User(_))
    }
    val commands = Commands()
    val result: Future[ValidatedNel[Message, Unit]] = Future.successful(Validated.valid({}))
    commands.checkinCommand.checkin(any[SortedSet[StagingDirectory]], any[Boolean])(any[MessageService]) returns result
    commands.checkoutCommand.checkout(any[SortedSet[FlacDirectory]], any[Boolean])(any[MessageService]) returns result
    commands.ownCommand.changeOwnership(any[OwnAction], any[SortedSet[User]], any[SortedSet[Either[StagingDirectory, FlacDirectory]]])(any[MessageService]) returns result
    commands.initialiseCommand.initialiseDb(any[MessageService]) returns result
    commands.multiDiscCommand.mutateMultiDiscAlbum(any[SortedSet[StagingDirectory]], any[MultiAction])(any[MessageService]) returns result

    val commandBuilder = new CommandBuilderImpl(
      commands.checkinCommand,
      commands.checkoutCommand,
      commands.ownCommand,
      commands.initialiseCommand,
      commands.multiDiscCommand,
      commands.calibrateCommand,
      userDao, repositories)
    Await.result(commandBuilder(jsValue), Duration(1, TimeUnit.SECONDS)).toEither.map(_ => commands)
  }

  def staging(path: String)(implicit fs: FileSystem): PathAndRepository = PathAndRepository(fs.getPath(path), StagingRepositoryType)
  def flac(path: String)(implicit fs: FileSystem): PathAndRepository = PathAndRepository(fs.getPath(path), FlacRepositoryType)

  def stagingObj: (String) => JsObject = pathObj(StagingRepositoryType)
  def flacObj: (String) => JsObject = pathObj(FlacRepositoryType)
  def pathObj(repositoryType: RepositoryType)(path: String): JsObject = {
    JsObject(Seq("path" -> JsString(path), "repositoryType" -> JsString(repositoryType.identifier)))
  }

  case class Commands(
                       checkinCommand: CheckinCommand = mock[CheckinCommand],
                       checkoutCommand: CheckoutCommand = mock[CheckoutCommand],
                       initialiseCommand: InitialiseCommand = mock[InitialiseCommand],
                       multiDiscCommand: MultiDiscCommand = mock[MultiDiscCommand],
                       calibrateCommand: CalibrateCommand = mock[CalibrateCommand],
                       ownCommand: OwnCommand = mock[OwnCommand]) {
    def noMore(): MatchResult[Unit] = {
      there were noMoreCallsTo(checkinCommand)
      there were noMoreCallsTo(checkoutCommand)
      there were noMoreCallsTo(initialiseCommand)
      there were noMoreCallsTo(multiDiscCommand)
      there were noMoreCallsTo(ownCommand)
      there were noMoreCallsTo(calibrateCommand)
    }
  }
}
