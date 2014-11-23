/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package common.joda

import java.util.Date

import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.joda.time.{DateTime => JDateTime}

import scala.util.Try

/**
 * A companion object for Joda DateTime that ensures all created dates have the correct time zone and chronology.
 * @author alex
 *
 */
object JodaDateTime {

  private val formatter: DateTimeFormatter = ISODateTimeFormat.dateTime()
  /**
   * Create a new date time.
   */
  def apply(m: Long) = new JDateTime(m)

  /**
   * Create a new date time.
   */
  def apply(d: Date) = new JDateTime(d)

  /**
   * Parse an ISO8601 formatter
   * @param s
   */
  def apply(s: String): Option[JDateTime] = Try(formatter.parseDateTime(s)).toOption

  def format(dateTime: JDateTime): String = formatter.print(dateTime)
}