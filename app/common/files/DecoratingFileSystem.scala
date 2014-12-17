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

package common.files

import com.typesafe.scalalogging.StrictLogging
import common.message.MessageService

/**
 * An implementation of {@link fileSystem} that decorates another {@link fileSystem}
 * @author alex
 *
 */
abstract class DecoratingFileSystem(implicit val fileLocationExtensions: FileLocationExtensions)
  extends FileSystem with StrictLogging {

  val delegate: FileSystem

  def wrap(block: => FileSystem => Unit)(fileLocations: FileLocation*): Unit = {
    before(fileLocations)
    try {
      block(delegate)
    }
    finally {
      after(fileLocations.filter(_.exists))
    }
  }

  override def move(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)(implicit messageService: MessageService): Unit =
    wrap(_.move(sourceFileLocation, targetFileLocation))(sourceFileLocation, targetFileLocation)

  override def copy(sourceFileLocation: FileLocation, targetFileLocation: FileLocation)(implicit messageService: MessageService): Unit =
    wrap(_.copy(sourceFileLocation, targetFileLocation))(targetFileLocation)

  override def remove(fileLocation: FileLocation)(implicit messageService: MessageService): Unit = {
    before(Seq(fileLocation))
    delegate.remove(fileLocation)
  }

  override def link(fileLocation: FileLocation, linkLocation: FileLocation)(implicit messageService: MessageService): Unit =
    wrap(_.link(fileLocation, linkLocation))(fileLocation, linkLocation)

  def before(fileLocations: Seq[FileLocation]): Unit

  def after(fileLocations: Seq[FileLocation]): Unit
}
