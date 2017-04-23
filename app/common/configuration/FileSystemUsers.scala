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

import java.nio.file.{Files, Path}
import javax.inject.Inject

import logging.ApplicationLogging

import scala.collection.JavaConversions._

/**
 * Get the users using Play common.configuration
 * Created by alex on 20/11/14.
 */
case class FileSystemUsers @Inject()(directories: Directories) extends Users with ApplicationLogging {

  val allUsers: Set[User] =
    Files.newDirectoryStream(directories.devicesPath).toSet.map((dir: Path) => User(dir.getFileName.toString))

  if (allUsers.isEmpty) {
    throw new IllegalStateException(s"Could not find any user directories under ${directories.devicesPath}")
  }
  else {
    logger.info(s"Found users ${allUsers.map(_.name).mkString(", ")}")
  }
}
