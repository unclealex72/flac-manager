/*
 * Copyright 2018 Alex Jones
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

package common.multi

/**
  * A trait that is used by the server to indicate whether albums with multiple discs are allowed or if they
  * should be explicitly split into different albums or joined into one album.
  **/
trait AllowMultiService {

  /**
    * Indicate whether albums with more than one disc are allowed.
    * @return True if albums with more than one disc are allowed, false otherwise.
    */
  def allowMulti: Boolean
}
