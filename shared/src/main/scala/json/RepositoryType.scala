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

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

/**
  * The different types of repository that clients need to understand.
  */
sealed trait RepositoryType extends EnumEntry

/**
  * The different types of repository that clients need to understand.
  */
object RepositoryType extends Enum[RepositoryType] {

  /**
    * The repository of flac files
    */
  case object FlacRepositoryType extends RepositoryType

  /**
    * The repository of staged flac files.
    */
  case object StagingRepositoryType extends RepositoryType

  val values: IndexedSeq[RepositoryType] = findValues
}
