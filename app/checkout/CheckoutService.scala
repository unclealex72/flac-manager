package checkout

import common.files.FlacFileLocation

/**
 * Created by alex on 16/11/14.
 */
trait CheckoutService {
  def checkout(locations: Seq[FlacFileLocation]): Unit

}
