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

package common.musicbrainz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * @author alex
 *
 */
public class MusicBrainzRetryFilter extends ClientFilter {

  private static final Logger log = LoggerFactory.getLogger(MusicBrainzRetryFilter.class);
  
  /**
   * The amount of time in milliseconds to wait between requests served with a
   * 503 response.
   */
  private final long throttleDelay;

  /**
   * The number of times a throttled request should be retried.
   */
  private final int throttleRetries;

  public MusicBrainzRetryFilter(long throttleDelay, int throttleRetries) {
    super();
    this.throttleDelay = throttleDelay;
    this.throttleRetries = throttleRetries;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
    int currentTry = 0;
    ClientResponse response;
    int throttleRetries = getThrottleRetries();
    do {
      if (currentTry != 0) {
        log.info("MusicBrainz was unavailable. Retrying.");
        sleep();
      }
      response = getNext().handle(cr);
      if (!Status.SERVICE_UNAVAILABLE.equals(response.getClientResponseStatus())) {
        return response;
      }
    } while (currentTry++ < throttleRetries);
    return response;
  }

  protected void sleep() {
    try {
      Thread.sleep(getThrottleDelay());
    }
    catch (InterruptedException e) {
      // Do nothing
    }
  }
  
  public long getThrottleDelay() {
    return throttleDelay;
  }

  public int getThrottleRetries() {
    return throttleRetries;
  }

}
