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
 * http://www.apache.org/licenses/LICENSE-2.0
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

package musicbrainz

import configuration.User

import scala.concurrent.Future

/**
 * <p>
 * An interface for classes that handle high level interactions with the
 * MusicBrainz web services.
 * </p>
 * <p>
 * When considering a user's collection, if a user has only one collection then
 * that shall be used. Otherwise, the user must have a collection called <i>All
 * my music</i> defined.
 * </p>
 *
 * @author alex
 *
 */
trait MusicBrainzClient {

  /**
   * The user agent to send to MusicBrainz.
   */
  val USER_AGENT = "FlacManager/6.0 ( https://github.com/dcs3apj/flac-manager )";

  /**
   * The query parameter required for POST requests.
   */
  val ID_PARAMETER = "client" -> "flacmanager-6.0"

  /**
   * The maximum number of release IDs allowed in a PUT or DELETE request.
   */
  val RELEASE_PATH_LIMIT = 100

  /**
   * The maximum size of a query.
   */
  val LIMIT = 100

  /**
   * The name of the user's collection.
   */
  val ALL_MY_MUSIC = "All my music"


  /**
   * Get all the releases owned by a user. If the user only has one collection
   * then this shall be used. Otherwise, only a collection called <i>All my
   * music</i> will be searched.
   *
   * @param user
   * The user who is doing the searching.
   * @return A list of all the MusicBrainz releases owned by the user.
   * @throws thrown if a unique collection cannot be found.
   */
  def relasesForOwner(user: User): Future[Traversable[String]]

  /**
   * Add releases to an owner's collection.
   * @param user The user whose collection needs changing.
   * @param newReleaseIds The new releases to add to the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  def addReleases(user: User, newReleaseIds: Traversable[String]): Future[Unit]

  /**
   * Remove releases from an owner's collection.
   * @param user The user whose collection needs changing.
   * @param oldReleaseIds The old releases to remove from the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  def removeReleases(user: User, oldReleaseIds: Traversable[String]): Future[Unit]
}
