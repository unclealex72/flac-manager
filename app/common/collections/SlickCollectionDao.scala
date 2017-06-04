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
package common.collections

import javax.inject.Inject

import common.changes.{Change, ChangeDao}
import common.configuration.User
import common.db.SlickDao
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SlickCollectionDao  @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                    (implicit val ec: ExecutionContext) extends CollectionDao with SlickDao {

  import dbConfig.profile.api._

  //noinspection TypeAnnotation
  val create = collectionItems.schema.create
  //noinspection TypeAnnotation
  val drop = collectionItems.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TCollection(_tableTag: Tag) extends Table[CollectionItem](_tableTag, "collectionItem") {
    def * = (
      id,
      user,
      releaseId) <> (CollectionItem.tupled, CollectionItem.unapply)

    val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val parentRelativePath: Rep[Option[String]] =
      column[Option[String]]("parentRelativePath", O.Length(512, varying = true), O.Default(None))
    val user: Rep[String] = column[String]("user", O.Length(128, varying = true))
    val releaseId: Rep[String] = column[String]("action", O.Length(128, varying = true))
  }

  /** Collection-like TableQuery object for table Game */
  lazy val collectionItems = new TableQuery(tag => new TCollection(tag))

  /**
    * Add releases to an owner's collection.
    *
    * @param user          The user whose collection needs changing.
    * @param newReleaseIds The new releases to add to the user's collection.
    */
  override def addReleases(user: User, newReleaseIds: Set[String]): Future[Unit] = dbConfig.db.run {
  DBIO.seq(
      collectionItems.map(ci => (ci.user, ci.releaseId)) ++= newReleaseIds.map(releaseId => (user.name, releaseId))
    )
  }

  /**
    * Remove releases from an owner's collection.
    *
    * @param user          The user whose collection needs changing.
    * @param oldReleaseIds The old releases to remove from the user's collection.
   */
  override def removeReleases(user: User, oldReleaseIds: Set[String]): Future[Unit] = dbConfig.db.run {
    DBIO.sequence(oldReleaseIds.toSeq.map { oldReleaseId =>
      collectionItems.
        filter(ci => ci.user === user.name && ci.releaseId === oldReleaseId).
        delete
    }).map(_ => {})
  }

  /**
    * Get all the releases and who owns them.
    *
    * @return All map of owners keyed by the album ID that they own.
    */
  override def allOwnersByRelease(): Future[Map[String, Seq[String]]] = dbConfig.db.run {
    collectionItems.result
  }.map(_.groupBy(_.user).mapValues(_.map(_.releaseId)))
}