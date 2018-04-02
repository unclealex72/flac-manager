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

package common.dates

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import cats.syntax.either._
import play.api.mvc.PathBindable

import scala.util.Try

/**
  * A wrapper around a [[ZonedDateTime]] that can be used in URLs.
  **/
case class DateTime(zonedDateTime: ZonedDateTime)

object DateTime {

  val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())

  /**
    * Allow [[DateTime]] objects to be used as java time objects.
    * @param dateTime The dateTime to convert.
    * @return The dateTime's wrapped zoned dateTime.
    */
  implicit def dateTimeToZonedDateTime(dateTime: DateTime): ZonedDateTime = dateTime.zonedDateTime

  /**
    * Allow dateTimes to be referenced in URLs.
    * @return A path binder allowing dateTimes to be referenced in URLs.
    */
  implicit val pathBinder: PathBindable[DateTime] = new PathBindable[DateTime] {

    override def bind(key: String, value: String): Either[String, DateTime] = {
      Try(DateTime(ZonedDateTime.parse(value, formatter))).toEither.leftMap(_.getMessage)

    }
    override def unbind(key: String, value: DateTime): String = formatter.format(value)
  }

}