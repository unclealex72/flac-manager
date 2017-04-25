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
package common.joda

import java.util.Date

import logging.ApplicationLogging
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.joda.time.{DateTime => JDateTime}

import scala.util.{Failure, Try}

/**
 * A companion object for Joda DateTime that ensures all created dates have the correct time zone and chronology.
 *
 */
object JodaDateTime extends ApplicationLogging {

  private val formatter: DateTimeFormatter = ISODateTimeFormat.dateTime()

  /**
    * Create a new date time.
    * @param m The number of milliseconds since the UNIX Epoch.
    * @return A new Joda DateTime
    */
  def apply(m: Long) = new JDateTime(m)

  /**
    * Create a new date time.
    * @param d A java date.
    * @return A new Joda DateTime
    */
  def apply(d: Date) = new JDateTime(d)

  /**
    * Parse a string for an ISO8601 date
    * @param s The string to parse.
    * @return A Joda DateTime or None if the string could not be parsed.
    */
  def apply(s: String): Option[JDateTime] = Try {
    formatter.parseDateTime(s)
  }.recoverWith { case (t: Throwable) =>
    logger.error(s"Could not parse $s", t)
    Failure(t)
  }.toOption

  /**
    * Format a Joda DateTime into ISO8601
    * @param dateTime The time to format.
    * @return The date formatted in to ISO8601 format.
    */
  def format(dateTime: JDateTime): String = formatter.print(dateTime)
}