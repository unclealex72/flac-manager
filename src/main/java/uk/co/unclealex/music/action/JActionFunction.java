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
 * An inherently stateful and thus non-thread safe {@link Function} that can transform {@link JAction}s into
 * values.
 *
 * @param <V> the value type
 * @author alex
 */
public class JActionFunction<V> extends JActionVisitor.Default implements Function<JAction, V> {

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
  public JActionFunction(V defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final V apply(JAction action) {
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
  public final void visit(JMoveAction moveAction) throws IOException {
    setValue(visitAndReturn(moveAction));
  }

  /**
   * Return a value for a {@link JMoveAction}.
   * @param moveAction The {@link JMoveAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JMoveAction moveAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JDeleteAction deleteAction) throws IOException {
    setValue(visitAndReturn(deleteAction));
  }

  /**
   * Return a value for a {@link JDeleteAction}.
   * @param deleteAction The {@link JDeleteAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JDeleteAction deleteAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JEncodeAction encodeAction) throws IOException {
    setValue(visitAndReturn(encodeAction));
  }

  /**
   * Return a value for a {@link EncodeAcion}.
   * @param encodeAction The {@link JEncodeAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JEncodeAction encodeAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JAddArtworkAction addArtworkAction) throws IOException {
    setValue(visitAndReturn(addArtworkAction));
  }

  /**
   * Return a value for a {@link JAddArtworkAction}.
   * @param addArtworkAction The {@link JAddArtworkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JAddArtworkAction addArtworkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JLinkAction linkAction) throws IOException {
    setValue(visitAndReturn(linkAction));
  }

  /**
   * Return a value for a {@link JLinkAction}.
   * @param linkAction The {@link JLinkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JLinkAction linkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JUnlinkAction unlinkAction) throws IOException {
    setValue(visitAndReturn(unlinkAction));
  }

  /**
   * Return a value for a {@unlink UnlinkAction}.
   * @param unlinkAction The {@unlink UnlinkAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JUnlinkAction unlinkAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JFailureAction failureAction) throws IOException {
    setValue(visitAndReturn(failureAction));
  }

  /**
   * Return a value for a {@link JFailureAction}.
   * @param failureAction The {@link JFailureAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JFailureAction failureAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JCoverArtAction coverArtAction) throws IOException {
    setValue(visitAndReturn(coverArtAction));
  }

  /**
   * Return a value for a {@link JCoverArtAction}.
   * @param coverArtAction The {@link JCoverArtAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JCoverArtAction coverArtAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JChangeOwnerAction changeOwnerAction) throws IOException {
    setValue(visitAndReturn(changeOwnerAction));
  }

  /**
   * Return a value for a {@link JChangeOwnerAction}.
   * @param changeOwnerAction The {@link JChangeOwnerAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JChangeOwnerAction changeOwnerAction) {
    return getDefaultValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JUpdateOwnershipAction updateOwnershipAction) throws IOException {
    setValue(visitAndReturn(updateOwnershipAction));
  }

  /**
   * Return a value for a {@link JUpdateOwnershipAction}.
   * @param updateOwnershipAction The {@link JUpdateOwnershipAction} that was visited.
   * @return The default Value.
   */
  protected V visitAndReturn(JUpdateOwnershipAction updateOwnershipAction) {
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
