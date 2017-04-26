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

import javax.inject.Inject

import org.joda.time.DateTime

/**
  * The default implementation of [[NowService]] that actually does get the current system date and time.
  */
class NowServiceImpl @Inject() extends NowService {

  /**
   * @inheritdoc
   */
  override def now(): DateTime = new DateTime()
}
