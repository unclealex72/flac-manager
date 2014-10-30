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

package sync.drive

/**
 * An abstract class for services that create sequences of tuples from cells taken
 * from lists of strings.
 * @author alex
 *
 */
abstract class AbstractStringCellMappingService[K, V](
                                                       /**
                                                        * The number of the column containing the map's keys. A negative value
                                                        * indicates that the column is taken from the end of the cells.
                                                        */
                                                       val keyColumn: Int,

                                                       /**
                                                        * The number of the column containing the map's values. A negative value
                                                        * indicates that the column is taken from the end of the cells.
                                                        */
                                                       val valueColumn: Int) {

  /**
   * Create a Pair whose keys and values are taken from a list of
   * strings.
   *
   * @throws java.io.IOException
   * Signals that an I/O exception has occurred.
   */
  def generateMap: Seq[(K, V)] = {
    val requiredNumberOfColumns = Math.max(Math.abs(keyColumn), Math.abs(valueColumn))
    generateLines.flatMap { line =>
      val cells: Array[String] = line.split( """\s+""").map(_.trim).filter(_.nonEmpty)
      val size = cells.size
      if (size < requiredNumberOfColumns) {
        None
      }
      else {
        val key = parseKey(cells(columnNumber(size, keyColumn)))
        val value = parseValue(cells(columnNumber(size, valueColumn)))
        (key, value) match {
          case (Some(key), Some(value)) => Some((key, value))
          case _ => None
        }
      }
    }
  }.toSeq

  /**
   * Calculate a column number given the total number of column.
   *
   * @param totalColumns
   * The total number of columns.
   * @param column
   * The column number to look for. If this is negative then it is
   * taken as one-based from the end of the cells.
   * @return A strictly positive column number.
   */
  def columnNumber(totalColumns: Int, column: Int): Int = {
    if (column < 0) totalColumns + column else column
  }

  /**
   * Parse a found key.
   *
   * @param key
   * The key to parse.
   * @return A new key of the correct type or null if this key should not be
   *         added to the map.
   */
  def parseKey(key: String): Option[K]

  /**
   * Parse a found value.
   *
   * @param value
   * The value to parse.
   * @return A new value of the correct type or null if this value should not be
   *         added to the map.
   */
  def parseValue(value: String): Option[V]

  /**
   * Generate the lines that will be parsed into a {@link com.google.common.collect.BiMap}.
   *
   * @return The lines that will be parsed into a { @link com.google.common.collect.BiMap}.
   * @throws java.io.IOException
   * Signals that an I/O exception has occurred.
   */
  def generateLines: Traversable[String]

}