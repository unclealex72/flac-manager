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
package common.changes

import java.nio.file.Paths

import com.typesafe.scalalogging.StrictLogging
import common.configuration.{TestDirectories, User}
import common.files._
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.specs2.mutable._
import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}

/**
 * @author alex
 *
 */
class SquerylChangeDaoSpec extends Specification with StrictLogging {

  object Dsl {

    val df: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")

    implicit def asDateTime(str: String) = df.parseDateTime(str)

    implicit class ChangeBuilderA(albumAndTitle: (String, String)) {
      def ownedBy(user: User) = (Paths.get(albumAndTitle._1, albumAndTitle._2).toString, user)
    }

    implicit class ChangeBuilderB(relativePathAndUser: (String, User)) {
      implicit val directories: TestDirectories = TestDirectories(Paths.get("/"), Paths.get("/"), Paths.get("/"), Paths.get("/"), Paths.get("/"))

      def fileLocationExtensions(dateTime: DateTime) = new TestFileLocationExtensions {
        override def lastModified(fileLocation: FileLocation): Long = dateTime.getMillis
      }

      def addedAt(dateTime: DateTime): Change = {
        Change.added(DeviceFileLocation(relativePathAndUser._2, relativePathAndUser._1))(fileLocationExtensions(dateTime))
      }

      def removedAt(dateTime: DateTime): Change = {
        Change.removed(DeviceFileLocation(relativePathAndUser._2, relativePathAndUser._1), dateTime)
      }
    }

    implicit def asUser(name: String) = User(name, "", "", Paths.get("/"))
  }

  import Dsl._


  class Context {

    val freddie: User = "Freddie"
    val brian: User = "Brian"

    val tearItUpAdded = ("The Works", "Tear it Up.mp3") ownedBy brian addedAt "05/09/1972 09:12:00"
    val bohemianRhapsodyRemoved = ("A Night at the Opera", "Bohemian Rhapsody.mp3") ownedBy freddie removedAt "05/09/1972 09:12:30"
    val myFairyKingAdded = ("Queen", "My Fairy King.mp3") ownedBy freddie addedAt "05/09/1972 09:13:00"
    val theNightComesDownAdded = ("Queen", "The Night Comes Down.mp3") ownedBy freddie addedAt "05/09/1972 09:13:00"
    val funnyHowLoveIsAdded = ("Queen II", "Funny How Love Is.mp3") ownedBy freddie addedAt "05/09/1972 09:13:00"
    val weWillRockYouRemoved = ("News of the World", "We Will Rock You.mp3") ownedBy brian removedAt "05/09/1972 09:13:30"
    val weAreTheChampionsAdded = ("News of the World", "We Are The Champions.mp3") ownedBy freddie addedAt "05/09/1972 09:14:00"
    val weAreTheChampionsRemoved = ("News of the World", "We Are The Champions.mp3") ownedBy freddie removedAt "05/09/1972 09:14:30"
  }

  "Getting all changes since a specific time for a user" should {
    "retrieve only changes for a user since a specific time" in txn { changeDao => context =>
      changeDao.getAllChangesSince(context.freddie, "05/09/1972 09:13:00") must contain(
        exactly(context.weAreTheChampionsRemoved, context.funnyHowLoveIsAdded, context.myFairyKingAdded, context.theNightComesDownAdded).inOrder)
    }
  }

  "Getting the changelog of changes" should {
    "retrieve at most the number of changes requested in change time order" in txn { changeDao => context =>
      changeDao.countChangelog(context.freddie) must be equalTo (3)
      val changeLogs = changeDao.changelog(context.freddie, 0, 2)
      changeLogs must contain(exactly(
        ChangelogItem("News of the World", "05/09/1972 09:14:00", "News of the World/We Are The Champions.mp3"),
        ChangelogItem("Queen", "05/09/1972 09:13:00", "Queen/My Fairy King.mp3")
      ).inOrder)
    }
  }


  /**
   * Wrap tests with database creation and transactions
   */
  def txn[B](block: ChangeDao => Context => B) = {
    Class forName "org.h2.Driver"
    SessionFactory.concreteFactory = Some(() => {
      val session = Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:mem:", "", ""),
        new H2Adapter)
      session.setLogger(logger.debug(_))
      session
    })
    val changeDao = new SquerylChangeDao()
    changeDao.tx { changeDao =>
      ChangeSchema.create
      val context = new Context()
      Seq(context.tearItUpAdded, context.bohemianRhapsodyRemoved, context.myFairyKingAdded, context.theNightComesDownAdded, context.funnyHowLoveIsAdded,
        context.weWillRockYouRemoved, context.weAreTheChampionsAdded, context.weAreTheChampionsRemoved).foreach(change => changeDao store change)
      block(changeDao)(context)
    }
  }

}