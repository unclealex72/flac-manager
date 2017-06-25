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

import common.async.CommandExecutionContext
import common.configuration.User
import common.db.SlickDao
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SlickCollectionDao  @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                    (implicit val commandExecutionContext: CommandExecutionContext) extends CollectionDao with SlickDao {

  import dbConfig.profile.api._

  //noinspection TypeAnnotation
  val create = collectionItems.schema.create
  //noinspection TypeAnnotation
  val drop = collectionItems.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TCollection(_tableTag: Tag) extends Table[CollectionItem](_tableTag, "COLLECTIONITEM") {
    def * = (
      id,
      user,
      releaseId,
      artist,
      album) <> (CollectionItem.tupled, CollectionItem.unapply)

    val id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    val parentRelativePath: Rep[Option[String]] =
      column[Option[String]]("PARENTRELATIVEPATH", O.Length(512, varying = true), O.Default(None))
    val user: Rep[User] = column[User]("USER", O.Length(128, varying = true))
    val releaseId: Rep[String] = column[String]("RELEASEID", O.Length(128, varying = true))
    val artist: Rep[String] = column[String]("ARTIST", O.Length(512, varying = true))
    val album: Rep[String] = column[String]("ALBUM", O.Length(512, varying = true))
  }

  /** Collection-like TableQuery object for table Game */
  lazy val collectionItems = new TableQuery(tag => new TCollection(tag))


  /**
    * @inheritdoc
    */
  override def addRelease(user: User, releaseId: String, artist: String, album: String): Future[Unit] = dbConfig.db.run {
    val collectionItemsQuery = collectionItems.filter(ci => ci.user === user && ci.releaseId === releaseId)
    val existingCollectionItemsResult = collectionItemsQuery.result
    existingCollectionItemsResult.flatMap[Int, NoStream, Nothing] { existingCollectionItems =>
      existingCollectionItems.headOption match {
        case Some(_) =>
          collectionItemsQuery.map(ci => (ci.artist, ci.album)).update((artist, album))
        case None =>
          collectionItems.map(ci => (ci.user, ci.releaseId, ci.artist, ci.album)) +=
            (user, releaseId, artist, album)
      }
    }
  }.map(_ => {})

  /**
    * @inheritdoc
    */
  override def removeRelease(user: User, releaseId: String): Future[Unit] = dbConfig.db.run {
    collectionItems.
      filter(ci => ci.user === user && ci.releaseId === releaseId).
      delete
  }.map(_ => {})

  /**
    * Get all the releases and who owns them.
    *
    * @return All map of owners keyed by the album ID that they own.
    */
  override def allOwnersByRelease(): Future[Map[String, Seq[User]]] = dbConfig.db.run {
    collectionItems.result
  }.map(_.groupBy(_.releaseId).mapValues(_.map(_.user)))
}