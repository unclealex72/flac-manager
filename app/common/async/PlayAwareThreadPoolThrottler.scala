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

package common.async

import checkin.{Throttler, ThrottlerOps}
import com.typesafe.scalalogging.StrictLogging
import javax.inject.Inject
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * A [[Throttler]] that uses Play configuration to get a value for `encoder.threads` to get the number of encoding
  * threads to use. It defaults to the number of available processors.
  **/
class PlayAwareThreadPoolThrottler @Inject() (configuration: Configuration,
                                              applicationLifecycle: ApplicationLifecycle)
                                             (implicit val commandExecutionContext: CommandExecutionContext) extends ThrottlerOps with StrictLogging {

  val throttler: Throttler = {
    val threads: Int = configuration.getOptional[Int]("encoder.threads").getOrElse(Runtime.getRuntime.availableProcessors())
    logger.info(s"Using $threads threads for encoding.")
    val throttler = new ThreadPoolThrottler(threads)
    applicationLifecycle.addStopHook { () =>
      Future {
        throttler.shutdown()
      }
    }
    throttler
  }
}
