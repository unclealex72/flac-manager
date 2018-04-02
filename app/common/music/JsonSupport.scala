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

package common.music

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import common.message.Message
import common.message.Messages.INVALID_TAGS
import play.api.libs.json.{JsNumber, JsString, JsValue}

import scala.collection.Map

/**
  * Created by alex on 12/06/17
  **/
object JsonSupport {

  def extract[V](fields: Map[String, JsValue], field: String)(f: PartialFunction[JsValue, ValidatedNel[Message, V]]): ValidatedNel[Message, Option[V]] = {
    fields.get(field) match {
      case Some(v) => f.lift(v) match {
        case Some(Valid(vv)) => Valid(Some(vv))
        case Some(Invalid(es)) => Invalid(es)
        case None => Valid(None)
      }
      case None => Valid(None)
    }
  }
  def strO(fields: Map[String, JsValue], field: String): ValidatedNel[Message, Option[String]] =
    extract(fields, field){ case JsString(string) => Valid(string) }
  def intO(fields: Map[String, JsValue], field: String): ValidatedNel[Message, Option[Int]] =
    extract(fields, field){ case JsNumber(num) => Valid(num.toInt) }
  def mandatory[V](f: (Map[String, JsValue], String) => ValidatedNel[Message, Option[V]])(fields: Map[String, JsValue], field: String): ValidatedNel[Message, V] = {
    f(fields, field) match {
      case Valid(Some(v)) => Valid(v)
      case Valid(None) => Validated.invalidNel(INVALID_TAGS(s"Cannot find field $field"))
      case Invalid(es) => Validated.invalid(es)
    }
  }
  val str: (Map[String, JsValue], String) => ValidatedNel[Message, String] = mandatory(strO)
  val int: (Map[String, JsValue], String) => ValidatedNel[Message, Int] = mandatory(intO)

}
