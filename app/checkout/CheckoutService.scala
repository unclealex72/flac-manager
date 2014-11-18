package checkout

import common.files.FlacFileLocation
import common.message.MessageService

import scala.collection.{SortedMap, SortedSet}

/**
 * Created by alex on 16/11/14.
 */
trait CheckoutService {
  def checkout(flacFileLocationsByParent: SortedMap[FlacFileLocation, SortedSet[FlacFileLocation]])(implicit messageService: MessageService)
}
