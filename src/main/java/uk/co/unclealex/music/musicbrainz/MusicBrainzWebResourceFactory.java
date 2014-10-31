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

import uk.co.unclealex.music.configuration.User;

import com.sun.jersey.api.client.WebResource;

/**
 * An interface for classes that create MusicBrainz friendly {@link WebResource}s.
 * @author alex
 *
 */
public interface MusicBrainzWebResourceFactory {

  /**
   * Create a new {@link WebResource} that can be used with MusicBrainz. The created resource will point
   * to the base resource, have a valid user agent, be aware of throttling and also be able to 
   * negotiate DIGEST authentication.
   *
   * @param user the user who is making the request or null if no authentication is needed.
   * @return A ready to use {@link WebResource}.
   */
  public WebResource webResource(User user);

}