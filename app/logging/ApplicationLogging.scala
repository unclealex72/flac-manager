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

package logging

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * A trait that can be mixed in to allow a class to log to Play's application logger.
  **/
trait ApplicationLogging {

  protected val logger: Logger =
    Logger(LoggerFactory.getLogger("application"))
}
