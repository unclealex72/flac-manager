/**
 * Copyright 2012 Alex Jones
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
 *
 * @author unclealex72
 *
 */

package uk.co.unclealex.music.sync.drive;

import java.io.IOException;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * An abstract class for services that create {@link BiMap}s from cells taken
 * from lists of strings.
 * 
 * @param <K>
 *          The type of keys produced.
 * @param <V>
 *          The type of values produced.
 * @author alex
 * 
 */
public abstract class AbstractStringCellMappingService<K, V> {

  /**
   * The number of the column containing the map's keys. A negative value
   * indicates that the column is taken from the end of the cells.
   */
  private final int keyColumn;

  /**
   * The number of the column containing the map's values. A negative value
   * indicates that the column is taken from the end of the cells.
   */
  private final int valueColumn;

  /**
   * The {@link BiMap} that contains the parsed keys and values.
   */
  private final BiMap<K, V> map = HashBiMap.create();

  /**
   * Instantiates a new abstract string cell mapping service.
   * 
   * @param keyColumn
   *          the key column
   * @param valueColumn
   *          the value column
   */
  public AbstractStringCellMappingService(final int keyColumn, final int valueColumn) {
    super();
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }

  /**
   * Create a {@link BiMap} whose keys and values are taken from a list of
   * strings.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void generateMap() throws IOException {
    final Splitter splitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().trimResults();
    final BiMap<K, V> map = getMap();
    final int keyColumn = getKeyColumn();
    final int valueColumn = getValueColumn();
    final int requiredNumberOfColumns = Math.max(Math.abs(keyColumn), Math.abs(valueColumn));
    for (final String line : generateLines()) {
      final List<String> cells = Lists.newArrayList(splitter.split(line));
      final int size = cells.size();
      if (size < requiredNumberOfColumns) {
        throw new IllegalStateException(String.format("String '%s' does not have enough columns", line));
      }
      final K key = parseKey(cells.get(columnNumber(size, keyColumn)));
      final V value = parseValue(cells.get(columnNumber(size, valueColumn)));
      if (key != null && value != null) {
        map.put(key, value);
      }
    }
  }

  /**
   * Calculate a column number given the total number of column.
   * 
   * @param totalColumns
   *          The total number of columns.
   * @param column
   *          The column number to look for. If this is negative then it is
   *          taken as one-based from the end of the cells.
   * @return A strictly positive column number.
   */
  protected int columnNumber(final int totalColumns, final int column) {
    return column < 0 ? totalColumns + column : column;
  }

  /**
   * Parse a found key.
   * 
   * @param key
   *          The key to parse.
   * @return A new key of the correct type or null if this key should not be
   *         added to the map.
   */
  public abstract K parseKey(String key) throws IOException;

  /**
   * Parse a found value.
   * 
   * @param value
   *          The value to parse.
   * @return A new value of the correct type or null if this value should not be
   *         added to the map.
   */
  public abstract V parseValue(String value) throws IOException;

  /**
   * Generate the lines that will be parsed into a {@link BiMap}.
   * 
   * @return The lines that will be parsed into a {@link BiMap}.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public abstract List<String> generateLines() throws IOException;

  /**
   * Gets the number of the column containing the map's keys.
   * 
   * @return the number of the column containing the map's keys
   */
  public int getKeyColumn() {
    return keyColumn;
  }

  /**
   * Gets the number of the column containing the map's values.
   * 
   * @return the number of the column containing the map's values
   */
  public int getValueColumn() {
    return valueColumn;
  }

  /**
   * Gets the {@link BiMap} that contains the parsed keys and values.
   * 
   * @return the {@link BiMap} that contains the parsed keys and values
   */
  public BiMap<K, V> getMap() {
    return map;
  }

}
