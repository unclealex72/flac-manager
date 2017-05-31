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
import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

/**
  * A [[Throttler]] that uses thread pools.
  **/
class ThreadPoolThrottler(val maximumParallelThreads: Int) extends Throttler {

  /**
    * A holder for an execution context and an executor.
    * @param executor The executor service to wrap.
    */
  private[ThreadPoolThrottler] case class ThrottlingContext(executor: ExecutorService) {
    val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(executor)
  }

  /**
    * An executor used for parallel execution.
    */
  val parallelThrottlingContext: ThrottlingContext =
    ThrottlingContext(Executors.newFixedThreadPool(maximumParallelThreads))

  /**
    * An executor used for sequential execution.
    */
  val sequentialThrottlingContext: ThrottlingContext =
    ThrottlingContext(Executors.newSingleThreadExecutor())

  override def parallel[T](block: => T): Future[T] = Future(block)(parallelThrottlingContext.executionContext)

  override def sequential[T](block: => T): Future[T] = Future(block)(sequentialThrottlingContext.executionContext)

  /**
    * Shutdown all executors.
    */
  def shutdown(): Unit = {
    Seq(parallelThrottlingContext, sequentialThrottlingContext).foreach { context =>
      context.executor.shutdownNow()
    }
  }
}
