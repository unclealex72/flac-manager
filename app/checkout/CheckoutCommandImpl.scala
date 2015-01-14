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

package checkout

import common.configuration.Directories
import common.files.{DirectoryService, FileLocationExtensions, FileSystem, FlacFileLocation}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}
import common.commands.CommandType
import common.commands.CommandType._

/**
 * Created by alex on 16/11/14.
 */
class CheckoutCommandImpl(val fileSystem: FileSystem, val directoryService: DirectoryService, val checkoutService: CheckoutService)
                         (implicit val directories: Directories, fileLocationExtensions: FileLocationExtensions) extends CheckoutCommand with Messaging {

  override def checkout(locations: Seq[FlacFileLocation], unown: Boolean)(implicit messageService: MessageService): CommandType = synchronous {
    val groupedFlacFileLocations = directoryService.groupFiles(locations)
    if (groupedFlacFileLocations.values.flatten.foldLeft(true)(validate)) {
      checkoutService.checkout(groupedFlacFileLocations, unown)
    }
  }

  def validate(result: Boolean, fl: FlacFileLocation)(implicit messageService: MessageService): Boolean = {
    val sfl = fl.toStagedFlacFileLocation
    if (sfl.exists) {
      log(OVERWRITE(fl, sfl))
      false
    }
    else {
      result
    }
  }

}
