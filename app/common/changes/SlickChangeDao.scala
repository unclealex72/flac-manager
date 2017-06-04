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

import java.time.Instant
import javax.inject.Inject

import common.configuration.User
import common.db.SlickDao
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
 * The Slick implementation of [[ChangeDao]].
 */
class SlickChangeDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                               (implicit val ec: ExecutionContext) extends ChangeDao with SlickDao {

  import dbConfig.profile.api._

  //noinspection TypeAnnotation
  val create = changes.schema.create
  //noinspection TypeAnnotation
  val drop = changes.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TChange(_tableTag: Tag) extends Table[Change](_tableTag, "change") {
    def * = (
      id,
      parentRelativePath,
      relativePath,
      at,
      user,
      action) <> (Change.tupled, Change.unapply)

    val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val parentRelativePath: Rep[Option[String]] =
      column[Option[String]]("parentRelativePath", O.Length(512, varying = true), O.Default(None))
    val relativePath: Rep[String] = column[String]("relativePath", O.Length(512,varying=true))
    val at: Rep[Instant] = column[Instant]("at")
    val user: Rep[String] = column[String]("user", O.Length(128, varying = true))
    val action: Rep[String] = column[String]("action", O.Length(128, varying = true))
  }

  /** Collection-like TableQuery object for table Game */
  lazy val changes = new TableQuery(tag => new TChange(tag))

  override def store(change: Change): Future[Change] = dbConfig.db.run {
    (changes returning changes).insertOrUpdate(change)
  }.map(_.getOrElse(change))

  override def countChanges(): Future[Int] = dbConfig.db.run {
    changes.size.result
  }

  override def getAllChangesSince(user: User, since: Instant): Future[Seq[Change]] = dbConfig.db.run {
    val latest =
      changes.filter(c => c.at >= since && c.user === user.name).groupBy(_.relativePath).map {
        case (relativePath, change) => (relativePath, change.map(_.at).max)
      }
    val changesSince = for {
      c <- changes
      l <- latest if l._1 === c.relativePath && l._2 === c.at
    } yield c
    changesSince.sortBy(c => (c.action.desc, c.relativePath.asc)).result
  }

  /**
    * Get a list of changelog items for a given user since a given amount of time. A changelog item
    * indicates that an album has been added or removed.
    *
    * @param user  The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of changelog items.
    */
  override def changelog(user: User, since: Instant): Future[Seq[ChangelogItem]] = dbConfig.db.run {
    changes.filter {
      c => c.action === "added" && c.user === user.name && c.parentRelativePath.isDefined && c.at >= since
    }.
    groupBy(_.parentRelativePath).
    map {
      case (parentRelativePath, c) =>
        (parentRelativePath, (c.map(_.at).min, c.map(_.relativePath).min))
    }.sortBy {
      case (parentRelativePath, c) =>
        (c._1.desc, parentRelativePath.asc)
    }.result
  }.map { results =>
    results.flatMap {
      case (Some(parentRelativePath), (Some(at), Some(relativePath))) =>
        Some(ChangelogItem(parentRelativePath, at, relativePath))
      case _ => None
    }
  }
}