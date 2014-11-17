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
import common.configuration.{Directories, User}
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

    implicit class ChangeBuilderA(relativePath: String) {
      def ownedBy(user: User) = (relativePath, user)
    }

    implicit class ChangeBuilderB(relativePathAndUser: (String, User)) {
      implicit val directories: Directories = Directories(Paths.get("/"), Paths.get("/"), Paths.get("/"), Paths.get("/"), Paths.get("/"))

      def fileLocationUtils(dateTime: DateTime) = new TestFileLocationExtensions {
        override def isDirectory(fileLocation: FileLocation): Boolean = false

        override def createTemporaryFileLocation()(implicit directories: Directories): TemporaryFileLocation = null

        override def lastModified(fileLocation: FileLocation): Long = dateTime.getMillis

        override def exists(fileLocation: FileLocation): Boolean = false
      }

      def addedAt(dateTime: DateTime): Change = {
        Change.added(DeviceFileLocation(relativePathAndUser._2, relativePathAndUser._1))(fileLocationUtils(dateTime))
      }

      def removedAt(dateTime: DateTime): Change = {
        Change.removed(DeviceFileLocation(relativePathAndUser._2, relativePathAndUser._1), dateTime)
      }
    }
    implicit def asUser(name: String) = User(name, "", "", "")
  }

  import Dsl._

  val freddie: User = "Freddie"
  val brian: User = "Brian"

  val tearItUpAdded = "Tear it Up.mp3" ownedBy brian addedAt "05/09/1972 09:12:00"
  val bohemianRhapsodyRemoved = "Bohemian Rhapsody.mp3" ownedBy freddie removedAt "05/09/1972 09:12:30"
  val myFairyKingAdded = "My Fairy King.mp3" ownedBy freddie addedAt "05/09/1972 09:13:00"
  val weWillRockYouRemoved = "We Will Rock You.mp3" ownedBy brian removedAt "05/09/1972 09:13:30"

  "Getting all changes since a specific time for a user" should {
    "retrieve only changes for a user since a specific time" in txn { changeDao =>
      Seq(tearItUpAdded, bohemianRhapsodyRemoved, myFairyKingAdded, weWillRockYouRemoved).foreach(change => changeDao store change)
      changeDao.getAllChangesSince(freddie, "05/09/1972 09:13:00").map(_.relativePath) must contain(exactly("My Fairy King.mp3"))
    }
  }

  /**
   * Wrap tests with database creation and transactions
   */
  def txn[B](block: ChangeDao => B) = {
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
      block(changeDao)
    }
  }

}