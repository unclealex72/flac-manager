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

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author alex
 *
 */
public class MusicBrainzRetryTester extends MusicBrainzTester {

  /**
   * @param rootResource
   */
  public MusicBrainzRetryTester() {
    super("ws-retry/root.txt");
  }

  @Test
  public void testNoRetries() {
    timesToReturn503 = 0;
    ClientResponse response = musicBrainzWebResourceFactory.webResource(user).path("brains.txt").get(ClientResponse.class);
    assertEquals("The wrong number of retries were attempted.", 0, countingMusicBrainzRetryFilter.retryCount);
    assertEquals("The wrong status was returned.", Status.OK, response.getClientResponseStatus());
  }

  @Test
  public void testOneRetry() {
    timesToReturn503 = 1;
    ClientResponse response = musicBrainzWebResourceFactory.webResource(user).path("brains.txt").get(ClientResponse.class);
    assertEquals("The wrong number of retries were attempted.", 1, countingMusicBrainzRetryFilter.retryCount);
    assertEquals("The wrong status was returned.", Status.OK, response.getClientResponseStatus());
  }

  @Test
  public void testTooManyRetries() {
    timesToReturn503 = 3;
    ClientResponse response = musicBrainzWebResourceFactory.webResource(user).path("brains.txt").get(ClientResponse.class);
    assertEquals("The wrong number of retries were attempted.", 2, countingMusicBrainzRetryFilter.retryCount);
    assertEquals("The wrong status was returned.", Status.SERVICE_UNAVAILABLE, response.getClientResponseStatus());
  }


}
