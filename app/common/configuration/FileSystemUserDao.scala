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

package common.configuration

import java.nio.file.Files
import javax.inject.Inject

import common.configuration.User._
import logging.ApplicationLogging

import scala.collection.JavaConversions._
import scala.collection.immutable.SortedSet

/**
  * Get the users using by searching for directories in the device repository.
  * @param directories The pointer to each repository.
  */
case class FileSystemUserDao @Inject()(directories: Directories) extends UserDao with ApplicationLogging {

  val users: SortedSet[User] =
    Files.newDirectoryStream(directories.devicesPath).foldLeft(SortedSet.empty[User]){ (users, dir) =>
      users + User(dir.getFileName.toString)
    }

  if (users.isEmpty) {
    throw new IllegalStateException(s"Could not find any user directories under ${directories.devicesPath}")
  }
  else {
    logger.info(s"Found users ${users.map(_.name).mkString(", ")}")
  }

  /**
    * @inheritdoc
    */
  override def allUsers(): Set[User] = users
}
