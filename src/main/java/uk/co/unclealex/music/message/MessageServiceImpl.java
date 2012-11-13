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

package uk.co.unclealex.music.message;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javax.inject.Inject;

import uk.co.unclealex.executable.streams.Stdout;
import uk.co.unclealex.music.files.FileLocation;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The default implementation of {@link MessageService}.
 * 
 * @author alex
 * 
 */
public class MessageServiceImpl implements MessageService {

  /**
   * The {@link ResourceBundle} containing messages to display.
   */
  private final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");

  /**
   * The {@link PrintWriter} where messages are to be written.
   */
  private final PrintWriter stdout;

  /**
   * Instantiates a new message service impl.
   * 
   * @param stdout
   *          the stdout
   */
  @Inject
  public MessageServiceImpl(@Stdout PrintWriter stdout) {
    super();
    this.stdout = stdout;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void printMessage(String template, Object... parameters) {
    String message;
    ResourceBundle resourceBundle = getResourceBundle();
    if (resourceBundle.containsKey(template)) {
      MessageFormat messageFormat = new MessageFormat(resourceBundle.getString(template));
      Object[] transformedParameters =
          Iterables.toArray(Iterables.transform(Arrays.asList(parameters), new PrintableFunction()), Object.class);
      message = messageFormat.format(transformedParameters);
    }
    else {
      message = template;
    }
    getStdout().println(message);
  }

  /**
   * A {@link Function} that is used to turn {@link Iterable}s into
   * comma-separated strings and {@link FileLocation} into readable paths.
   * 
   * @author alex
   * 
   */
  class PrintableFunction implements Function<Object, Object> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object apply(Object input) {
      if (input instanceof Iterable) {
        List<?> items = Lists.newArrayList((Iterable) input);
        int finalIndex = items.size() - 1;
        StringBuilder builder = new StringBuilder();
        ResourceBundle resourceBundle = getResourceBundle();
        for (ListIterator<?> iter = items.listIterator(); iter.hasNext(); ) {
          int index = iter.nextIndex();
          Object item = iter.next();
          if (index == finalIndex) {
            builder.append(resourceBundle.getString("finalJoiner"));
          }
          else if (index != 0) {
            builder.append(resourceBundle.getString("joiner"));
          }
          builder.append(apply(item));
        }
        return builder.toString();
      }
      else if (input instanceof FileLocation) {
        return ((FileLocation) input).resolve().toString();
      }
      else {
        return input;
      }
    }

  }

  /**
   * Gets the {@link ResourceBundle} containing messages to display.
   * 
   * @return the {@link ResourceBundle} containing messages to display
   */
  public ResourceBundle getResourceBundle() {
    return resourceBundle;
  }

  /**
   * Gets the {@link PrintWriter} where messages are to be written.
   * 
   * @return the {@link PrintWriter} where messages are to be written
   */
  public PrintWriter getStdout() {
    return stdout;
  }

}
