package checkout

import common.configuration.Directories
import common.files.FileLocationImplicits._
import common.files.{DirectoryService, FileLocationExtensions, FileSystem, FlacFileLocation}
import common.message.MessageTypes._
import common.message.{MessageService, Messaging}

import scala.collection.SortedSet

/**
 * Created by alex on 16/11/14.
 */
class CheckoutCommandImpl(val fileUtils: FileSystem, val directoryService: DirectoryService, val checkoutService: CheckoutService)
                         (implicit val directories: Directories, fileLocationUtils: FileLocationExtensions) extends CheckoutCommand with Messaging {

  type Validation = Either[Unit, Seq[FlacFileLocation]]

  override def checkout(locations: Seq[FlacFileLocation])(implicit messageService: MessageService): Unit = {
    val groupedFlacFileLocations = directoryService.groupFiles(locations)
    val flacFileLocations = groupedFlacFileLocations.foldLeft(SortedSet.empty[FlacFileLocation])(_ ++ _._2)
    val emptyValidation: Validation = Right(Seq.empty)
    flacFileLocations.foldLeft(emptyValidation)(validate) match {
      case Right(validFlacFiles) => {
        checkoutService.checkout(validFlacFiles)
      }
      case _ => {
        // Do nothing
      }
    }
  }

  def validate(result: Validation, fl: FlacFileLocation)(implicit messageService: MessageService): Validation = {
    val sfl = fl.toStagedFlacFileLocation
    if (sfl.exists) {
      log(OVERWRITE(fl, sfl))
      Left({})
    }
    else {
      for (r <- result.right) yield r :+ fl
    }
  }

}
