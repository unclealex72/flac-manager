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
package musicbrainz;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import configuration.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

/**
 * A base class for testing MusicBrainz client functionality.
 *
 * @author alex
 */
public class MusicBrainzTestContext {

    private static final Logger log = LoggerFactory.getLogger(MusicBrainzTestContext.class);

    /**
     * The name of the root resource to look for when creating a server.
     */
    private final String rootResource;


    public MusicBrainzTestContext(String rootResource) {
        super();
        this.rootResource = rootResource;
    }

    Server server;

    int port;

    MusicBrainzClientImpl musicBrainzClient;

    User user = new User("brian", "Brian", "may", "");

    List<String> requestLog = new ArrayList<>();

    public MusicBrainzTestContext setup() throws Exception {
        server = new Server(0);
        log.info("Starting new server at http://localhost:" + port);
        Servlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                checkUserAgent(req);
                URI baseUri;
                try {
                    baseUri = getClass().getClassLoader().getResource(getRootResource()).toURI();
                } catch (URISyntaxException e) {
                    throw new IOException(e);
                }
                String simpleRequestUri = req.getRequestURI().substring(6);
                List<String> uriParts = Lists.newArrayList(simpleRequestUri);
                List<String> parameterNames = Lists.newArrayList(Iterators.forEnumeration(req.getParameterNames()));
                Collections.sort(parameterNames);
                for (String parameterName : parameterNames) {
                    if (!parameterName.equals(musicBrainzClient.ID_PARAMETER()._1())) {
                        uriParts.add(parameterName);
                        uriParts.addAll(Arrays.asList(req.getParameterValues(parameterName)));
                    }
                }
                if (simpleRequestUri.indexOf('.') < 0) {
                    uriParts.add("xml");
                }
                URL urlToRetrieve = baseUri.resolve(Joiner.on('.').join(uriParts)).toURL();
                InputStream in;
                try {
                    in = urlToRetrieve.openStream();
                } catch (FileNotFoundException e) {
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
                } finally {
                    Files.deleteIfExists(tmp);
                }
            }

            @Override
            protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
                log(req);
            }

            @Override
            protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
                log(req);
            }

            protected void log(HttpServletRequest req) throws ServletException {
                checkUserAgent(req);
                requestLog.add(req.getMethod() + ":" + req.getRequestURI());
            }

            protected void checkUserAgent(HttpServletRequest req) throws ServletException {
                String userAgent = req.getHeader("User-Agent");
                if (!musicBrainzClient.USER_AGENT().equals(userAgent)) {
                    throw new ServletException("The wrong user agent was sent: " + userAgent);
                }
            }
        };
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setSecurityHandler(digestAuth(user.musicBrainzUserName(), user.musicBrainzPassword(), "musicbrainz.org"));
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = new ServletHolder(servlet);
        servletHolder.setInitOrder(0);
        context.addServlet(servletHolder, "/*");
        server.start();
        port = (Integer) FieldUtils.readField(server.getConnectors()[0], "_localPort", true);
        musicBrainzClient = new MusicBrainzClientImpl("localhost:" + port);
        return this;
    }

    protected final SecurityHandler digestAuth(String username, String password, String realm) {

        HashLoginService l = new HashLoginService();
        l.putUser(username, Credential.getCredential(password), new String[]{"user"});
        l.setName(realm);

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__DIGEST_AUTH);
        constraint.setRoles(new String[]{"user"});
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

    public void shutdown() throws Exception {
        server.stop();
    }

    public String getRootResource() {
        return rootResource;
    }
}
