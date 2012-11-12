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

package uk.co.unclealex.music.action;

import java.io.IOException;

import com.google.common.base.Function;

/**
 * An inherently stateful and thus non-thread safe {@link Function} that can transform {@link Action}s into
 * values.
 *
 * @param <V> the value type
 * @author alex
 */
public class ActionFunction<V> extends ActionVisitor.Default implements Function<Action, V> {

  /**
   * The default value to return if no other is specified. 
   */
  private final V defaultValue;
  
  /**
   * The value to return.
   */
  private V value;
  
  
  /**
   * Instantiates a new action function.
   *
   * @param defaultValue the default value
   */
  public ActionFunction(V defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final V apply(Action action) {
    try {
      action.accept(this);
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void visit(ProtectAction protectAction) throws IOException {
    setValue(visitAndReturn(protectAction));
  }

  /**
   * Return a value for a {@link ProtectAction}.
   * @param protectAction The {@link ProtectAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(ProtectAction protectAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(UnprotectAction unprotectAction) throws IOException {
    setValue(visitAndReturn(unprotectAction));
  }

  /**
   * Return a value for a {@link UnprotectAction}.
   * @param unprotectAction The {@link UnprotectAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(UnprotectAction unprotectAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void visit(MoveAction moveAction) throws IOException {
    setValue(visitAndReturn(moveAction));
  }

  /**
   * Return a value for a {@link MoveAction}.
   * @param moveAction The {@link MoveAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(MoveAction moveAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(DeleteAction deleteAction) throws IOException {
    setValue(visitAndReturn(deleteAction));
  }

  /**
   * Return a value for a {@link DeleteAction}.
   * @param deleteAction The {@link DeleteAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(DeleteAction deleteAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(EncodeAction encodeAction) throws IOException {
    setValue(visitAndReturn(encodeAction));
  }

  /**
   * Return a value for a {@link EncodeAcion}.
   * @param encodeAction The {@link EncodeAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(EncodeAction encodeAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(AddArtworkAction addArtworkAction) throws IOException {
    setValue(visitAndReturn(addArtworkAction));
  }

  /**
   * Return a value for a {@link AddArtworkAction}.
   * @param addArtworkAction The {@link AddArtworkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(AddArtworkAction addArtworkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(LinkAction linkAction) throws IOException {
    setValue(visitAndReturn(linkAction));
  }

  /**
   * Return a value for a {@link LinkAction}.
   * @param linkAction The {@link LinkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(LinkAction linkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(UnlinkAction unlinkAction) throws IOException {
    setValue(visitAndReturn(unlinkAction));
  }

  /**
   * Return a value for a {@unlink UnlinkAction}.
   * @param unlinkAction The {@unlink UnlinkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(UnlinkAction unlinkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(FailureAction failureAction) throws IOException {
    setValue(visitAndReturn(failureAction));
  }

  /**
   * Return a value for a {@link FailureAction}.
   * @param failureAction The {@link FailureAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(FailureAction failureAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(CoverArtAction coverArtAction) throws IOException {
    setValue(visitAndReturn(coverArtAction));
  }

  /**
   * Return a value for a {@link CoverArtAction}.
   * @param coverArtAction The {@link CoverArtAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(CoverArtAction coverArtAction) {
    return getDefaultValue();
  }


  /**
   * Gets the value to return.
   *
   * @return the value to return
   */
  public V getValue() {
    return value;
  }

  /**
   * Sets the value to return.
   *
   * @param value the new value to return
   */
  public void setValue(V value) {
    this.value = value;
  }

  /**
   * Gets the default value to return if no other is specified.
   *
   * @return the default value to return if no other is specified
   */
  public V getDefaultValue() {
    return defaultValue;
  }
}
