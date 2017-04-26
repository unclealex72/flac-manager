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

package common.now

import org.joda.time.DateTime

/**
  * A simple trait for getting the current time. Implementing this trait only means you have to a
  * time that can identified by the `now()` method; it does not have to be the current date and time.
  */
trait NowService {

  /**
   * Get the current date and time.
   * @return The current date and time.
   */
  def now(): DateTime
}
