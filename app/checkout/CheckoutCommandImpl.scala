package checkout

import common.configuration.Directories
import common.files.{DirectoryService, FileLocationExtensions, FileSystem, FlacFileLocation}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

/**
 * Created by alex on 16/11/14.
 */
class CheckoutCommandImpl(val fileSystem: FileSystem, val directoryService: DirectoryService, val checkoutService: CheckoutService)
                         (implicit val directories: Directories, fileLocationExtensions: FileLocationExtensions) extends CheckoutCommand with Messaging {

  override def checkout(locations: Seq[FlacFileLocation], unown: Boolean)(implicit messageService: MessageService): Unit = {
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
