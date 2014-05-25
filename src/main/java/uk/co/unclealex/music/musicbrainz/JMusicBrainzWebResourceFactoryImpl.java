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

import static com.sun.jersey.client.apache.config.ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import uk.co.unclealex.music.configuration.JUser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * The default implementation of {@link JMusicBrainzClient}. This class takes
 * into consideration rate limiting as described at <a
 * href="http://musicbrainz.org/doc/XML_Web_Service/Rate_Limiting">
 * http://musicbrainz.org/doc/XML_Web_Service/Rate_Limiting</a>. It also adds
 * the ability to parse XML into JDOM {@link Document}s.
 * 
 * @author alex
 * 
 */
public class JMusicBrainzWebResourceFactoryImpl implements JMusicBrainzWebResourceFactory, MessageBodyReader<Document> {

  /** The user agent to send to MusicBrainz. */
  static String USER_AGENT = "FlacManager/5.0 ( https://github.com/dcs3apj/flac-manager )";

  /**
   * The base resource of the MusicBrainz server.
   */
  private final String baseResource;

  /**
   * The {@link JMusicBrainzRetryFilter} used to make sure calls can be
   * throttled.
   */
  private final JMusicBrainzRetryFilter musicBrainzRetryFilter;

  /**
   * Instantiates a new default music brainz client.
   * 
   * @param baseResource
   *          the base resource
   * @param musicBrainzRetryFilter
   *          the music brainz retry filter
   */
  @Inject
  public JMusicBrainzWebResourceFactoryImpl(
          @JMusicBrainzBaseResource String baseResource,
          JMusicBrainzRetryFilter musicBrainzRetryFilter) {
    super();
    this.baseResource = baseResource;
    this.musicBrainzRetryFilter = musicBrainzRetryFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WebResource webResource(JUser user) {
    ApacheHttpClientConfig cc = new DefaultApacheHttpClientConfig();
    cc.getSingletons().add(this);
    cc.getProperties().put(PROPERTY_PREEMPTIVE_AUTHENTICATION, true);
    if (user != null) {
      cc.getState().setCredentials("musicbrainz.org", null, -1, user.getMusicBrainzUserName(), user.getMusicBrainzPassword());
    }
    Client client = ApacheHttpClient.create(cc);
    client.addFilter(getMusicBrainzRetryFilter());
    ClientFilter userAgentClientFilter = new ClientFilter() {
      @Override
      public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        cr.getHeaders().add(HttpHeaders.USER_AGENT, USER_AGENT);
        return getNext().handle(cr);
      }
    };
    client.addFilter(userAgentClientFilter);
    return client.resource(getBaseResource());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Document.class.equals(type);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Document readFrom(
      Class<Document> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream) throws IOException, WebApplicationException {
    SAXBuilder builder = new SAXBuilder();
    try {
      return builder.build(entityStream);
    }
    catch (JDOMException e) {
      throw new WebApplicationException(e, Status.NOT_ACCEPTABLE.getStatusCode());
    }
  }
  
  /**
   * Gets the base resource of the MusicBrainz server.
   * 
   * @return the base resource of the MusicBrainz server
   */
  public String getBaseResource() {
    return baseResource;
  }

  /**
   * Gets the {@link JMusicBrainzRetryFilter} used to make sure calls can be
   * throttled.
   * 
   * @return the {@link JMusicBrainzRetryFilter} used to make sure calls can be
   *         throttled
   */
  public JMusicBrainzRetryFilter getMusicBrainzRetryFilter() {
    return musicBrainzRetryFilter;
  }
}
