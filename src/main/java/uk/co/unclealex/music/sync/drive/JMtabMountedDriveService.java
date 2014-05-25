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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import com.google.common.collect.BiMap;

/**
 * An instance of {@link JMountedDriveService} that uses <code>/etc/mtab</code>
 * to find which drives are mounted.
 * 
 * @author alex
 * 
 */
public class JMtabMountedDriveService extends JAbstractStringCellMappingService<Path, Path> implements
        JMountedDriveService {

  public JMtabMountedDriveService() {
    super(1, 0);
  }

  @PostConstruct
  public void initialise() throws IOException {
    generateMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BiMap<Path, Path> listDevicesByMountPoint() {
    return getMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path parseKey(final String key) {
    return parse(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path parseValue(final String value) {
    return parse(value);
  }

  /**
   * Parse a cell, unescaping all octets.
   * 
   * @param cell
   *          The cell to parse.
   * @return The path the cell represents or null if this cell does not
   *         represent an absolute path.
   */
  protected Path parse(final String cell) {
    final String nonOctalCell = processOctal(cell);
    if (!nonOctalCell.startsWith("/")) {
      return null;
    }
    else {
      return Paths.get(nonOctalCell);
    }
  }

  /**
   * Convert octal escaped characters in strings.
   * 
   * @param line
   *          The line to convert.
   * @return A new string that contains normal characters
   */
  protected String processOctal(final String line) {
    final Pattern octalPattern = Pattern.compile("\\\\([0-7]{3})");
    final StringBuffer sb = new StringBuffer();
    final Matcher matcher = octalPattern.matcher(line);
    while (matcher.find()) {
      final Character ch = (char) Integer.parseInt(matcher.group(1), 8);
      matcher.appendReplacement(sb, Matcher.quoteReplacement(ch.toString()));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> generateLines() throws IOException {
    return Files.readAllLines(Paths.get("/etc", "mtab"), Charset.defaultCharset());
  }
}
