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

package common.message

/**
 * A trait for building [[MessageService]]s.
 */
trait MessageServiceBuilder {

  /**
    * Create a [[MessageService]]
    * @return A [[MessageService]].
    */
  def build: MessageService

  /**
    * Build a [[MessageService]] that prints messages.
    * @param printer The callback that will print messages.
    * @return A [[MessageServiceBuilder]] that will build a [[MessageService]] that prints messages
    *         using the given callback.
    */
  def withPrinter(printer: String => Unit): MessageServiceBuilder

  /**
    * Build a [[MessageService]] that handles exceptions.
    * @param handler The callback that will handle exceptions.
    * @return A [[MessageServiceBuilder]] that will build a [[MessageService]] that handles exceptions
    *         using the given callback.
    */
  def withExceptionHandler(handler: Throwable => Unit): MessageServiceBuilder

  /**
    * Build a [[MessageService]] that handles feedback finishing.
    * @param onFinish The callback that will be called on finishing.
    * @return A [[MessageServiceBuilder]] that will build a [[MessageService]] that handles finishing
    *         using the given callback.
    */
  def withOnFinish(onFinish: () => Unit): MessageServiceBuilder
}