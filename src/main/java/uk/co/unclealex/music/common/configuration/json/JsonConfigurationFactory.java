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

package uk.co.unclealex.music.common.configuration.json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import uk.co.unclealex.music.common.configuration.Configuration;
import uk.co.unclealex.music.common.configuration.ConfigurationFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A {@link ConfigurationFactory} that reads a {@link ConfigurationBean} from a
 * JSON stream.
 * 
 * @author alex
 * 
 */
public class JsonConfigurationFactory implements ConfigurationFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Configuration load(InputStream in) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule testModule =
        new SimpleModule("ConfigurationModule", new Version(1, 0, 0, null, null, null)).addDeserializer(
            Path.class,
            new PathDeserializer());
    mapper.registerModule(testModule);
    ObjectReader reader = mapper.reader(ConfigurationBean.class);
    return reader.readValue(in);
  }

  /**
   * Deserialize a {@link Path} object by using its string representation.
   * 
   * @author alex
   * 
   */
  static class PathDeserializer extends JsonDeserializer<Path> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Path deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      return Paths.get(jp.getValueAsString());
    }

  }
}
