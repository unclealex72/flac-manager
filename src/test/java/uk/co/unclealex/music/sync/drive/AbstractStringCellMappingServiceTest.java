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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;


import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class AbstractStringCellMappingServiceTest {

  class TestStringCellMappingService extends AbstractStringCellMappingService<String, String> {

    public TestStringCellMappingService() {
      super(1, -3);
    }

    @Override
    public String parseKey(final String key) {
      return "key:" + key;
    }

    @Override
    public String parseValue(final String value) {
      return "value:" + value;
    }

    @Override
    public List<String> generateLines() throws IOException {
      return Lists.newArrayList(
          "one two three four five six seven eight",
          "ten eleven twelve thirteen fourteen fifteen");
    }

  }

  @Test
  public void testCellMapping() throws IOException {
    final TestStringCellMappingService service = new TestStringCellMappingService();
    service.generateMap();
    final BiMap<String, String> map = service.getMap();
    assertThat("The generated map had the wrong number of elements.", map.entrySet(), hasSize(2));
    assertThat(
        "The generated map has the wrong elements.",
        map,
        allOf(hasEntry("key:two", "value:six"), hasEntry("key:eleven", "value:thirteen")));
  }

}
