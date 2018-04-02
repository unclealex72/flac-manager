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

package controllers

import cats.data.ValidatedNel
import common.message.{Message, MessageService}
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
  * A trait for building a [[Future]] from a JSON RPC payload sent from a client.
  */
trait CommandBuilder {

  /**
    * Try to create a [[Future]] for a JSON RPC payload.
    * @param jsValue The RPC payload.
    * @return Either a list of errors or a function that will create a [[Future]] when a
    *         [[MessageService]] is supplied.
    */
  def apply(jsValue: JsValue)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]]
}
