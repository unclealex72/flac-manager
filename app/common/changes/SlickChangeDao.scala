/*
 * Copyright 2018 Alex Jones
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
package common.changes

import java.nio.file.Path
import java.time.Instant

import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.configuration.User
import common.db.SlickDao
import common.files.Extension
import common.message.Messages.ADD_CHANGE
import common.message.{MessageService, Messaging}
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

/**
 * The Slick implementation of [[ChangeDao]].
 */
class SlickChangeDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                               (implicit val commandExecutionContext: CommandExecutionContext) extends ChangeDao with SlickDao with StrictLogging with Messaging {

  import dbConfig.profile.api._

  implicit val changeTypeIsomorphism: Isomorphism[ChangeType, String] = new Isomorphism(
    _.action,
    action => ChangeType.values.find(ct => ct.action == action).get
  )

  //noinspection TypeAnnotation
  val create = changes.schema.create
  //noinspection TypeAnnotation
  val drop = changes.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TChange(_tableTag: Tag) extends Table[Change](_tableTag, "CHANGE") {
    def * = (
      id,
      parentRelativePath,
      relativePath,
      at,
      user,
      extension,
      action) <> ((Change.apply _).tupled, Change.unapply)

    val id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    val parentRelativePath: Rep[Option[Path]] =
      column[Option[Path]]("PARENTRELATIVEPATH", O.Length(512, varying = true), O.Default(None))
    val relativePath: Rep[Path] = column[Path]("RELATIVEPATH", O.Length(512,varying=true))
    val at: Rep[Instant] = column[Instant]("AT")
    val user: Rep[User] = column[User]("USER", O.Length(128, varying = true))
    val extension: Rep[Extension] = column[Extension]("EXTENSION", O.Length(128, varying = true))
    val action: Rep[ChangeType] = column[ChangeType]("ACTION", O.Length(128, varying = true))
  }

  val ADDED : Rep[ChangeType] = LiteralColumn("added").mapTo[ChangeType]

  /** Collection-like TableQuery object for table Game */
  lazy val changes = new TableQuery(tag => new TChange(tag))

  override def store(change: Change)(implicit messageService: MessageService): Future[Unit] = dbConfig.db.run {
    log(ADD_CHANGE(change))
    changes += change
  }.map(_ => {})

  private val countChangesQuery = Compiled { changes.size }

  override def countChanges(): Future[Int] = dbConfig.db.run {
    countChangesQuery.result
  }

  private val getAllChangesSinceQuery = Compiled { (user: Rep[User], extension: Rep[Extension], since: Rep[Instant]) =>
    val latest =
      changes.filter(c => c.at >= since && c.user === user && c.extension === extension).groupBy(_.relativePath).map {
        case (relativePath, change) => (relativePath, change.map(_.at).max)
      }
    val changesSince = for {
      c <- changes
      l <- latest if l._1 === c.relativePath && l._2 === c.at
    } yield c
    changesSince.sortBy(c => (c.action.desc, c.relativePath.asc))
  }

  override def getAllChangesSince(user: User, extension: Extension, since: Instant): Future[Seq[Change]] = dbConfig.db.run {
    getAllChangesSinceQuery(user, extension, since).result
  }

  private val changelogQuery = Compiled { (user: Rep[User], extension: Rep[Extension], since: Rep[Instant]) =>
    changes.filter {
      c => c.action === LiteralColumn[ChangeType](AddedChange) && c.user === user && c.extension === extension && c.parentRelativePath.isDefined && c.at >= since
    }.
      groupBy(_.parentRelativePath).
      map {
        case (parentRelativePath, c) =>
          (parentRelativePath, (c.map(_.at).min, c.map(_.relativePath).min))
      }.sortBy {
      case (parentRelativePath, c) =>
        (c._1.desc, parentRelativePath.asc)
    }
  }
  /**
    * Get a list of changelog items for a given user since a given amount of time. A changelog item
    * indicates that an album has been added or removed.
    *
    * @param user  The user making the request.
    * @param since The earliest time to search for changes.
    * @return A list of changelog items.
    */
  override def changelog(user: User, extension: Extension, since: Instant): Future[Seq[ChangelogItem]] = dbConfig.db.run {
    changelogQuery(user, extension, since).result
  }.map { results =>
    results.flatMap {
      case (Some(parentRelativePath), (Some(at), Some(relativePath))) =>
        Some(ChangelogItem(parentRelativePath, at, relativePath))
      case _ => None
    }
  }
}