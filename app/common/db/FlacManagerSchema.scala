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
package common.db

import common.changes.Change
import common.collections.CollectionItem
import common.db.SquerylEntryPoint._
import org.squeryl.Schema

/**
 * The Squeryl schema definition for this application.
 * @author alex
 *
 */
object FlacManagerSchema extends Schema {

  val changes = table[Change]("change")
  val collectionItems = table[CollectionItem]("collectionitem")

  /**
   * Column constraints
   */
  on(changes)(c => declare(c.id is autoIncremented))
  on(collectionItems)(c => declare(c.id is autoIncremented))

}