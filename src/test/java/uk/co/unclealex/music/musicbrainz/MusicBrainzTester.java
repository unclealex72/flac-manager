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
package uk.co.unclealex.music.musicbrainz;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.configuration.Device;
import uk.co.unclealex.music.configuration.User;
import uk.co.unclealex.music.configuration.json.UserBean;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * A base class for testing MusicBrainz client functionality.
 * @author alex
 * 
 */
public abstract class MusicBrainzTester {

  private static final Logger log = LoggerFactory.getLogger(MusicBrainzTester.class);

  /**
   * The name of the root resource to look for when creating a server.
   */
  private final String rootResource;
  
  
  public MusicBrainzTester(String rootResource) {
    super();
    this.rootResource = rootResource;
  }

  Server server;

  int port;

  int timesToReturn503;

  CountingMusicBrainzRetryFilter countingMusicBrainzRetryFilter;

  MusicBrainzWebResourceFactory musicBrainzWebResourceFactory;

  User user = new UserBean("brian", "Brian", "may", new ArrayList<Device>());
  
  @Before
  public void setup() throws Exception {
    timesToReturn503 = 0;
    port = FreePortFinder.findFreePort();
    server = new Server(port);
    log.info("Starting new server at http://localhost:" + port);
    Servlet servlet = new HttpServlet() {
      @Override
      protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userAgent = req.getHeader(HttpHeaders.USER_AGENT);
        assertEquals("The wrong user agent was sent.", MusicBrainzWebResourceFactoryImpl.USER_AGENT, userAgent);
        if (timesToReturn503 != 0) {
          timesToReturn503--;
          resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        else {
          URI baseUri;
          try {
            baseUri = getClass().getClassLoader().getResource(getRootResource()).toURI();
          }
          catch (URISyntaxException e) {
            throw new IOException(e);
          }
          String simpleRequestUri = req.getRequestURI().substring(1);
          List<String> uriParts = Lists.newArrayList(simpleRequestUri);
          List<String> parameterNames = Lists.newArrayList(Iterators.forEnumeration(req.getParameterNames()));
          Collections.sort(parameterNames);
          for (String parameterName : parameterNames) {
            uriParts.add(parameterName);
            uriParts.addAll(Arrays.asList(req.getParameterValues(parameterName)));
          }
          if (simpleRequestUri.indexOf('.') < 0) {
            uriParts.add("xml");
          }
          URL urlToRetrieve = baseUri.resolve(Joiner.on('.').join(uriParts)).toURL();
          InputStream in;
          try {
            in = urlToRetrieve.openStream();
          }
          catch (FileNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
          }
          Path tmp = Files.createTempFile("music-brainz-tester-", ".unknown");
          try {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            in.close();
            resp.setContentType(Files.probeContentType(tmp));
            resp.setContentLength((int) Files.size(tmp));
            OutputStream out = resp.getOutputStream();
            Files.copy(tmp, out);
            out.flush();
            out.close();
          }
          finally {
            Files.deleteIfExists(tmp);
          }
        }
      }
    };
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setSecurityHandler(digestAuth("Brian", "may", "musicbrainz.org"));
    context.setContextPath("/");
    server.setHandler(context);
    ServletHolder servletHolder = new ServletHolder(servlet);
    servletHolder.setInitOrder(0);
    context.addServlet(servletHolder, "/*");
    server.start();
    countingMusicBrainzRetryFilter = new CountingMusicBrainzRetryFilter(0, 2);
    musicBrainzWebResourceFactory =
        new MusicBrainzWebResourceFactoryImpl("http://localhost:" + port + "/", countingMusicBrainzRetryFilter);
  }

  protected final SecurityHandler digestAuth(String username, String password, String realm) {

    HashLoginService l = new HashLoginService();
    l.putUser(username, Credential.getCredential(password), new String[] { "user" });
    l.setName(realm);

    Constraint constraint = new Constraint();
    constraint.setName(Constraint.__DIGEST_AUTH);
    constraint.setRoles(new String[] { "user" });
    constraint.setAuthenticate(true);

    ConstraintMapping cm = new ConstraintMapping();
    cm.setConstraint(constraint);
    cm.setPathSpec("/*");

    ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
    csh.setAuthenticator(new DigestAuthenticator());
    csh.setRealmName(realm);
    csh.addConstraintMapping(cm);
    csh.setLoginService(l);

    return csh;
  }

  @After
  public void shutdown() throws Exception {
    server.stop();
  }

  static class CountingMusicBrainzRetryFilter extends MusicBrainzRetryFilter {

    public int retryCount;

    public CountingMusicBrainzRetryFilter(
        @MusicBrainzThrottleDelay long throttleDelay,
        @MusicBrainzThrottleRetries int throttleRetries) {
      super(throttleDelay, throttleRetries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void sleep() {
      retryCount++;
    }
  }

  public String getRootResource() {
    return rootResource;
  }
}
