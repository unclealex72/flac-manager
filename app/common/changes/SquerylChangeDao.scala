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

import javax.inject.Inject

import common.configuration.User
import common.db.SquerylEntryPoint._
import common.db.FlacManagerSchema._
import org.joda.time.DateTime
import org.squeryl.dsl.ast.LogicalBoolean

/**
 * The Squeryl implementation of [[ChangeDao]].
 */
class SquerylChangeDao @Inject() extends ChangeDao {

  /**
   * Run code within a transaction.
   * @param block The code to run.
   */
  def tx[T](block: ChangeDao => T): T = inTransaction {
    block(this)
  }

  /**
    * @inheritdoc
    */
  def store(change: Change): Change = inTransaction {
    changes.insertOrUpdate(change)
    change
  }

  /**
    * @inheritdoc
    */
  override def getAllChangesSince(user: User, since: DateTime): List[Change] = inTransaction {
    val latest = from(changes)(c => where(c.at >= since and user.name === c.user) groupBy c.relativePath compute max(c.at))
    from(latest, changes)((l, c) =>
      where(user.name === c.user and l.key === c.relativePath and l.measures === c.at) select c orderBy (c.action desc, c.relativePath.asc))
  }

  /**
    * @inheritdoc
    */
  override def countChanges(): Long = inTransaction {
    from(changes)(c => compute(count(c.id)))
  }

  /**
    * @inheritdoc
    */
  override def countChangesSince(user: User, since: DateTime): Long = inTransaction {
    from(changes)(c => where(user.name === c.user and c.at >= since) compute count(c.id))
  }

  private def isPartOfChangeLog(c: Change, user: User): LogicalBoolean =
    c.action === "added" and c.user === user.name and c.parentRelativePath.isNotNull

  /**
    * @inheritdoc
    */
  def changelog(user: User, since: DateTime): List[ChangelogItem] = inTransaction {
    val groupedChanges =
      from(changes)(c => where(isPartOfChangeLog(c, user) and c.at >= since)
        groupBy c.parentRelativePath
        compute(min(c.at), min(c.relativePath)(optionStringTEF)) orderBy(min(c.at) desc, min(c.parentRelativePath) asc))
    val changelogItems =
      for {
        g <- groupedChanges.iterator
        parentRelativePath <- g.key
        at <- g.measures._1
        relativePath <- g.measures._2
      } yield ChangelogItem(parentRelativePath, at, relativePath)
    changelogItems.toList
  }

  private def countFilteredChangelog(filter: Change => LogicalBoolean): Long = inTransaction {
    from(changes)(c =>
      where(filter(c))
        compute countDistinct(c.parentRelativePath)
    ).single.measures
  }

  /**
    * @inheritdoc
    */
  def countChangelog(user: User): Long = countFilteredChangelog(c => isPartOfChangeLog(c, user))

  /**
    * @inheritdoc
    */
  def countChangelogSince(user: User, since: DateTime): Long = countFilteredChangelog(c => isPartOfChangeLog(c, user) and c.at >= since)

}