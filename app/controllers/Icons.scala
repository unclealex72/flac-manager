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

import play.api.mvc.{Action, AnyContent, Controller}

/**
  * A controller to show icons at the root level.
  * Created by alex on 19/04/17
  **/
class Icons extends Controller {

  def at(path: String, file: String, aggressiveCaching: Boolean = false): Action[AnyContent] =
    Assets.at(path, file, aggressiveCaching)

}
