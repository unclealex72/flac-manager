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

package common.configuration

import play.api.mvc.PathBindable

/**
  * A user who has entries in the devices repository.
  *
  * @param name the user's name that is to be used to identify their devices and to
  *         be used when changing ownership
  */
case class User(name: String)

/**
  * Ordering for Users.
  */
object User {

  /**
    * Order users by their name.
    */
  implicit val ordering: Ordering[User] = Ordering.by(_.name)

  /**
    * Allow users to be referenced in URLs.
    * @return A path binder allowing users to be referenced in URLs.
    */
  implicit val pathBinder: PathBindable[User] = new PathBindable[User] {
    override def bind(key: String, value: String): Either[String, User] = Right(User(value))
    override def unbind(key: String, value: User): String = value.name
  }
}