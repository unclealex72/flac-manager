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

import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntBinaryOperator

import org.specs2.mutable.Specification

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


/**
  * Created by alex on 30/05/17
  **/
class ThreadPoolThrottlerSpec extends Specification {

  case class Counter(delay: FiniteDuration) {

    private val concurrentCounter = new AtomicInteger(0)
    private val max = new AtomicInteger(0)

    def enter(): Unit = {
      val newValue = concurrentCounter.incrementAndGet()
      max.getAndAccumulate(newValue, new IntBinaryOperator {
        override def applyAsInt(left: Int, right: Int): Int = Math.max(left, right)
      })
      Thread.sleep(delay.toMillis)
      concurrentCounter.decrementAndGet()
    }

    def maximum(): Int = max.get()
  }

  "The thread pool throttler" should {
    "not allow more than one thread at a time to run in a sequential block" in {
      val throttler = new ThreadPoolThrottler(8)
      try {
        val parallelCounter = Counter(10.milli)
        val sequentialCounter = Counter(20.milli)
        val eventualResponse = Future.sequence(Range(1, 10).map { _ =>
          for {
            _ <- throttler.parallel(parallelCounter.enter())
            _ <- throttler.sequential(sequentialCounter.enter())
            _ <- throttler.parallel(parallelCounter.enter())
          } yield {}
        })
        Await.result(eventualResponse, 2000.milli)
        parallelCounter.maximum() must be_>(1)
        parallelCounter.maximum() must be_<=(8)
        sequentialCounter.maximum() must be_==(1)
      }
      finally {
        throttler.shutdown()
      }
    }
  }
}
