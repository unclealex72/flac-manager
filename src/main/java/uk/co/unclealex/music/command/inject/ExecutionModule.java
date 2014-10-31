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

package uk.co.unclealex.music.command.inject;

import uk.co.unclealex.music.command.CommandLine;
import uk.co.unclealex.music.command.Execution;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * The Guice {@link Module} for components common to all execution based common.commands.
 * 
 * @param <C>
 *          the generic type
 * @param <E>
 *          the element type
 * @author alex
 */
public abstract class ExecutionModule<C extends CommandLine, E extends Execution<C>> extends AbstractModule {

  /**
   * The class of the {@link Execution} to use in the command.
   */
  private final Class<E> executionClass;

  /**
   * The {@link TypeLiteral} to which the execution class should be bound.
   */
  private final Key<Execution<C>> executionKey;

  /**
   * Instantiates a new common module.
   * 
   * @param executionClass
   *          the execution class
   * @param executionKey
   *          the execution key
   */
  public ExecutionModule(Class<E> executionClass, TypeLiteral<Execution<C>> executionKey) {
    super();
    this.executionClass = executionClass;
    this.executionKey = Key.get(executionKey);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    install(new CommonModule());
    // Make sure java.util.logging is directed to SLF4J. This module is
    // guaranteed to be called so here's as good a place
    // as any.
    bind(getExecutionKey()).to(getExecutionClass());
  }

  /**
   * Gets the class of the {@link Execution} to use in the command.
   * 
   * @return the class of the {@link Execution} to use in the command
   */
  public Class<E> getExecutionClass() {
    return executionClass;
  }

  /**
   * Gets the {@link Key} to which the execution class should be bound.
   * 
   * @return the {@link Key} to which the execution class should be bound
   */
  public Key<Execution<C>> getExecutionKey() {
    return executionKey;
  }

}
