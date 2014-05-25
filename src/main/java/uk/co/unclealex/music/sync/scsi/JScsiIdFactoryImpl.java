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

package uk.co.unclealex.music.sync.scsi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default implementation of {@link JScsiIdFactory}.
 * 
 * @author alex
 * 
 */
public class JScsiIdFactoryImpl implements JScsiIdFactory {

  /**
   * The {@link Pattern} used to parse <code>[h:c:t:l]</code> strings.
   */
  private static final Pattern HCTL = Pattern.compile("\\[([0-9]+):([0-9]+):([0-9]+):([0-9]+)\\]");

  /**
   * {@inheritDoc}
   */
  @Override
  public JScsiId create(final int hba, final int channel, final int targetId, final int lun) {
    return new JScsiId(hba, channel, targetId, lun);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JScsiId create(final String hctl) {
    final Matcher m = HCTL.matcher(hctl);
    if (!m.matches()) {
      throw new IllegalArgumentException(String.format("String '%s' is in in [h:c:t:l] format.", hctl));
    }
    return create(
        Integer.parseInt(m.group(1)),
        Integer.parseInt(m.group(2)),
        Integer.parseInt(m.group(3)),
        Integer.parseInt(m.group(4)));
  }
}
