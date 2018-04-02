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

package json

import enumeratum.Enum

import scala.collection.immutable.IndexedSeq

/**
  * The different ways of handling a multi disc album.
  */
sealed trait MultiAction extends IdentifiableEnumEntry

/**
  * The different types of repository that clients need to understand.
  */
object MultiAction extends Enum[MultiAction] {

  /**
    * An object that indicates multi disc albums should be split.
    */
  case object Split extends MultiAction {
    override val identifier: String = "split"
  }

  /**
    * An object that indicates multi disc albums should be joined.
    */
  case object Join extends MultiAction {
    override val identifier: String = "join"
  }

  val values: IndexedSeq[MultiAction] = findValues
}
