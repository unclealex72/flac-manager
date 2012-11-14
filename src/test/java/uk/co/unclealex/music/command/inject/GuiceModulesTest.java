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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.executable.streams.Stdout;
import uk.co.unclealex.music.command.CheckinCommandLine;
import uk.co.unclealex.music.command.CheckoutCommandLine;
import uk.co.unclealex.music.command.CommandLine;
import uk.co.unclealex.music.command.Execution;
import uk.co.unclealex.music.command.checkin.CheckinExecution;
import uk.co.unclealex.music.command.checkin.CheckinModule;
import uk.co.unclealex.music.command.checkout.CheckoutExecution;
import uk.co.unclealex.music.command.checkout.CheckoutModule;
import uk.co.unclealex.music.configuration.Configuration;
import uk.co.unclealex.music.configuration.json.AmazonConfigurationBean;
import uk.co.unclealex.music.configuration.json.ConfigurationBean;
import uk.co.unclealex.music.configuration.json.PathsBean;
import uk.co.unclealex.music.configuration.json.UserBean;

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
    runTest(new CheckinModule(), CheckinExecution.class, new TypeLiteral<Execution<CheckinCommandLine>>() {
    });
  }

  @Test
  public void testCheckout() {
    runTest(new CheckoutModule(), CheckoutExecution.class, new TypeLiteral<Execution<CheckoutCommandLine>>() {
    });
  }

  public <C extends CommandLine, E extends Execution<C>> void runTest(
      Module guiceModule,
      Class<? extends Execution<?>> expectedExecutionClass,
      TypeLiteral<E> typeLiteral) {
    Module stdoutModule = new AbstractModule() {

      @Override
      protected void configure() {
        bind(PrintWriter.class).annotatedWith(Stdout.class).toInstance(new PrintWriter(System.out));
      }
    };
    ExternalModule externalModule = new ExternalModule() {
      @Override
      protected void bindConfiguration() {
        ConfigurationBean configurationBean =
            new ConfigurationBean(
                new PathsBean(null, null, null, null),
                (List<UserBean>) new ArrayList<UserBean>(),
                new AmazonConfigurationBean(null, null, null));
        bind(Configuration.class).toInstance(configurationBean);
      }
    };
    Injector injector = Guice.createInjector(guiceModule, stdoutModule, externalModule);
    E execution = injector.getInstance(Key.get(typeLiteral));
    Assert.assertEquals("The returned execution had the wrong class.", expectedExecutionClass, execution.getClass());
  }
}
