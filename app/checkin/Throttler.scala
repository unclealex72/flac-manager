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

package checkin

import scala.concurrent.Future

/**
  * A trait that allows certain actions to throttled to one thread and others to be run in parallel. This
  * can be used to make sure that chaining futures can have choke-points where a user wants to restrict one type
  * of future so that it cannot be called twice at the same time.
  **/
trait Throttler {

  /**
    * Run a task in parallel.
    * @param block The code to run.
    * @tparam T The type to return.
    * @return A future containing the result of running the code block.
    */
  def parallel[T](block: => T): Future[T]

  /**
    * Run a task sequentially.
    * @param block The code to run.
    * @tparam T The type to return.
    * @return A future containing the result of running the code block.
    */
  def sequential[T](block: => T): Future[T]
}

/**
  * A mixin used to add syntactic sugar for the [[Throttler]] trait.
  */
trait ThrottlerOps extends Throttler {

  /**
    * A throttler to delegate to.
    */
  val throttler: Throttler

  override def parallel[T](block: =>T): Future[T] = throttler.parallel(block)

  override def sequential[T](block: =>T): Future[T] = throttler.sequential(block)

}