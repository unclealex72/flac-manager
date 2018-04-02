/*
 * Copyright 2017 Alex Jones
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

package controllers

import javax.inject.Inject
import play.api.mvc._

/**
  * A controller to show icons at the root level. This is to make rendering favicons easier.
  **/
class Icons @Inject() (val controllerComponents: ControllerComponents, val assets: Assets) extends BaseController {

  /**
    * Return an asset.
    * @param path The root path where to look for assets.
    * @param file The file supplied by the client.
    * @return A asset.
    */
  def at(path: String, file: String): Action[AnyContent] =
    assets.at(path, file, aggressiveCaching = true)

}
