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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.executable.streams.Stderr;
import uk.co.unclealex.executable.streams.Stdin;
import uk.co.unclealex.executable.streams.Stdout;
import uk.co.unclealex.music.command.JCheckinCommandLine;
import uk.co.unclealex.music.command.JCheckoutCommandLine;
import uk.co.unclealex.music.command.JCommandLine;
import uk.co.unclealex.music.command.JExecution;
import uk.co.unclealex.music.command.checkin.JCheckinExecution;
import uk.co.unclealex.music.command.checkin.JCheckinModule;
import uk.co.unclealex.music.command.checkout.JCheckoutExecution;
import uk.co.unclealex.music.command.checkout.JCheckoutModule;
import uk.co.unclealex.music.configuration.JConfiguration;
import uk.co.unclealex.music.configuration.json.AmazonConfigurationBean;
import uk.co.unclealex.music.configuration.json.JConfigurationBean;
import uk.co.unclealex.music.configuration.json.JPathsBean;
import uk.co.unclealex.music.configuration.json.JUserBean;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author alex
 * 
 */
public class GuiceModulesTest {

  @Test
  public void testCheckin() {
    runTest(new JCheckinModule(), JCheckinExecution.class, new TypeLiteral<JExecution<JCheckinCommandLine>>() {
    });
  }

  @Test
  public void testCheckout() {
    runTest(new JCheckoutModule(), JCheckoutExecution.class, new TypeLiteral<JExecution<JCheckoutCommandLine>>() {
    });
  }

  public <C extends JCommandLine, E extends JExecution<C>> void runTest(
      Module guiceModule,
      Class<? extends JExecution<?>> expectedExecutionClass,
      TypeLiteral<E> typeLiteral) {
    Module stdoutModule = new AbstractModule() {

      @Override
      protected void configure() {
        bind(PrintWriter.class).annotatedWith(Stdout.class).toInstance(new PrintWriter(System.out));
        bind(PrintStream.class).annotatedWith(Stdout.class).toInstance(System.out);
        bind(PrintStream.class).annotatedWith(Stderr.class).toInstance(System.err);
        bind(OutputStream.class).annotatedWith(Stdin.class).toInstance(new ByteArrayOutputStream());
      }
    };
    JExternalModule externalModule = new JExternalModule() {
      @Override
      protected void bindConfiguration() {
        JConfigurationBean configurationBean =
            new JConfigurationBean(
                new JPathsBean(null, null, null, null),
                (List<JUserBean>) new ArrayList<JUserBean>(),
                new AmazonConfigurationBean(null, null, null));
        bind(JConfiguration.class).toInstance(configurationBean);
      }
    };
    Injector injector = Guice.createInjector(guiceModule, stdoutModule, externalModule);
    E execution = injector.getInstance(Key.get(typeLiteral));
    Assert.assertEquals("The returned execution had the wrong class.", expectedExecutionClass, execution.getClass());
  }
}
