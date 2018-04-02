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

package common.db

import java.nio.file.{Path, Paths}
import java.sql.Timestamp
import java.time.Instant

import common.configuration.User
import common.files.Extension
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
  * A trait that adds JodaTime support to Slick-based DAOs.
  **/
trait SlickDao extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  implicit val dateTimeIsomorphism: Isomorphism[Instant, Timestamp] = new Isomorphism(
    dt => new Timestamp(dt.toEpochMilli),
    ts => Instant.ofEpochMilli(ts.getTime)
  )

  implicit val userColumnIsomorphism: Isomorphism[User, String] = new Isomorphism(_.name, User(_))

  implicit val extensionColumnIsomorphism: Isomorphism[Extension, String] =
    new Isomorphism(_.extension, ext => Extension.values.find(extension => extension.extension == ext).get)

  implicit val pathColumnIsomorphism: Isomorphism[Path, String] = new Isomorphism(_.toString, Paths.get(_))
}
