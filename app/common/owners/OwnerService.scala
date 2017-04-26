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

package common.owners

import common.configuration.User
import common.message.MessageService
import common.music.Tags

/**
  * Allow the listing and changing of which albums should be included on a user's device.
  */
trait OwnerService {

  /**
    * List all collections.
    * @return A function that, given [[Tags]], will return the set of [[User]]s who own them.
    */
  def listCollections(): Tags => Set[User]

  /**
    * Add a set of albums to a user's collection.
    * @param user The user who's collection will be changed.
    * @param tags The tags containing the albums that will be added.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    */
  def own(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit

  /**
    * Remove a set of albums from a user's collection.
    * @param user The user who's collection will be changed.
    * @param tags The tags containing the albums that will be removed.
    * @param messageService The [[MessageService]] used to report progress and log errors.
    */
  def unown(user: User, tags: Set[Tags])(implicit messageService: MessageService): Unit
}
