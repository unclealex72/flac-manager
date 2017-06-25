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
package common.changes

import java.nio.file.{Path, Paths}
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}

import cats.data.Validated
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import common.async.{CommandExecutionContext, GlobalExecutionContext}
import common.configuration.User
import common.files.{DeviceFile, TagsContainer}
import common.message.Messages.INVALID_TAGS
import common.message.{MessageService, NoOpMessageService}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable._
import org.specs2.specification.ForEach
import play.api.db.slick.{DatabaseConfigProvider, DbName, DefaultSlickApi}
import play.api.inject.DefaultApplicationLifecycle
import play.api.{Configuration, Environment, Mode}
import slick.basic.{BasicProfile, DatabaseConfig}
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
/**
 * @author alex
 *
 */
class SlickChangeDaoSpec extends Specification with ForEach[Db] with StrictLogging {

  sequential

  import Dsl._

  "Getting all changes since a specific time for a user" should {
    "retrieve only changes for a user since a specific time" in { db: Db =>
      implicit val (changeDao, ee): (ChangeDao, ExecutionEnv) = (db.changeDao, db.ee)

      changeDao.getAllChangesSince(freddie, "05/09/1972 09:13:00").map(_.map(c => c.copy(id = 0))) must contain(
        exactly(weAreTheChampionsRemoved, funnyHowLoveIsAdded, myFairyKingAdded, theNightComesDownAdded).inOrder).await
    }
  }

  "Getting the changelog of changes" should {
    "retrieve at most the number of changes requested in change time order" in { db: Db =>
      implicit val (changeDao, ee): (ChangeDao, ExecutionEnv) = (db.changeDao, db.ee)

      val changeLogs = changeDao.changelog(freddie, "05/09/1972 09:13:00")
      changeLogs must contain(exactly(
        ChangelogItem("News of the World", "05/09/1972 09:14:00", "News of the World/We Are The Champions.mp3"),
        ChangelogItem("Queen II", "05/09/1972 09:13:20", "Queen II/Funny How Love Is.mp3"),
          ChangelogItem("Queen", "05/09/1972 09:13:00", "Queen/My Fairy King.mp3")
      ).inOrder).await
    }
  }

  val freddie: User = "Freddie"
  val brian: User = "Brian"

  val tearItUpAdded: Change = ("The Works", "Tear it Up.mp3") ownedBy brian addedAt "05/09/1972 09:12:00"
  val bohemianRhapsodyRemoved: Change = ("A Night at the Opera", "Bohemian Rhapsody.mp3") ownedBy freddie removedAt "05/09/1972 09:12:30"
  val myFairyKingAdded: Change = ("Queen", "My Fairy King.mp3") ownedBy freddie addedAt "05/09/1972 09:13:00"
  val theNightComesDownAdded: Change = ("Queen", "The Night Comes Down.mp3") ownedBy freddie addedAt "05/09/1972 09:13:10"
  val funnyHowLoveIsAdded: Change = ("Queen II", "Funny How Love Is.mp3") ownedBy freddie addedAt "05/09/1972 09:13:20"
  val weWillRockYouRemoved: Change = ("News of the World", "We Will Rock You.mp3") ownedBy brian removedAt "05/09/1972 09:13:30"
  val weAreTheChampionsAdded: Change = ("News of the World", "We Are The Champions.mp3") ownedBy freddie addedAt "05/09/1972 09:14:00"
  val weAreTheChampionsRemoved: Change = ("News of the World", "We Are The Champions.mp3") ownedBy freddie removedAt "05/09/1972 09:14:30"

  implicit val messageService: MessageService = NoOpMessageService(this)

  def foreach[R: AsResult](f: Db => R): Result = {
    val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
    implicit val ec = ee.ec

    val result = for {
      changeDaoAndConfig <- openDatabaseTransaction
      changeDao = changeDaoAndConfig._1
      config = changeDaoAndConfig._2
      _ <- populateDatabase(changeDao)
      result <- Future.successful {
        try {
          AsResult(f(Db(changeDao, ee)))
        }
        finally {
          closeDatabaseTransaction(changeDao, config)
        }
      }
    } yield result
    Await.result(result, 1.minute)
  }

  // create and close a transaction
  def openDatabaseTransaction(implicit ec: ExecutionContext): Future[(SlickChangeDao, DatabaseConfig[JdbcProfile])] = {
    implicit val cec = new GlobalExecutionContext with CommandExecutionContext
    val config: Config = ConfigFactory.parseString(
      """
        |play.slick.db.config="slick.dbs"
        |play.slick.db.default="default"
        |slick.dbs.default {
        |  profile = "slick.jdbc.SQLiteProfile$"
        |  db {
        |    driver = "org.sqlite.JDBC"
        |    url = "jdbc:sqlite::memory:"
        |  }
        |}""".stripMargin)

    val slickApi = new DefaultSlickApi(
      Environment(Paths.get("/").toFile, getClass.getClassLoader, Mode.Dev),
      Configuration(config),
      new DefaultApplicationLifecycle())
    val provider = new DatabaseConfigProvider {
      def get[P <: BasicProfile]: DatabaseConfig[P] = slickApi.dbConfig[P](DbName("default"))
    }
    val changeDao = new SlickChangeDao(provider) {
      val dbConfigForTesting: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
    }
    changeDao.dbConfigForTesting.db.run(changeDao.create).map(_ => (changeDao, changeDao.dbConfigForTesting))
  }

  def populateDatabase(changeDao: ChangeDao)(implicit ec: ExecutionContext): Future[Unit] = {
    Seq(
      tearItUpAdded,
      bohemianRhapsodyRemoved,
      myFairyKingAdded,
      theNightComesDownAdded,
      funnyHowLoveIsAdded,
      weWillRockYouRemoved,
      weAreTheChampionsAdded,
      weAreTheChampionsRemoved).foldLeft(Future.successful({})) { (acc, change) =>
      acc.flatMap(_ => changeDao.store(change))
    }

  }

  def closeDatabaseTransaction(changeDao: SlickChangeDao, dbConfigForTesting: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext): Future[Unit] = {
    dbConfigForTesting.db.run(changeDao.drop)
  }

}

case class Db(changeDao: ChangeDao, ee: ExecutionEnv)

object Dsl {

  val df: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault())

    implicit def stringAsInstant(str: String): Instant = ZonedDateTime.parse(str, df).toInstant
    implicit def stringAsUser(str: String): User = User(str)
    implicit def stringAsPath(str: String): Path = Paths.get(str)

  implicit class ChangeBuilderA(albumAndTitle: (String, String)) {
    def ownedBy(user: User): (String, User) = (Paths.get(albumAndTitle._1, albumAndTitle._2).toString, user)
  }

  implicit class ChangeBuilderB(relativePathAndUser: (String, User)) {

    case class SimpleDeviceFile(user: User, relativePath: Path, lastModified: Instant) extends DeviceFile {
      override val readOnly: Boolean = false
      override val rootPath: Path = Paths.get("/")
      override val basePath: Path = Paths.get("/")
      override val absolutePath: Path = relativePath
      override val tags: TagsContainer = () => Validated.invalidNel(INVALID_TAGS("Nope"))
      override val exists: Boolean = false
    }
    def addedAt(instant: Instant): Change = {
      Change.added(SimpleDeviceFile(relativePathAndUser._2, Paths.get(relativePathAndUser._1), instant))
    }

    def removedAt(instant: Instant): Change = {
      Change.removed(SimpleDeviceFile(relativePathAndUser._2, Paths.get(relativePathAndUser._1), instant), instant)
    }
  }
}
