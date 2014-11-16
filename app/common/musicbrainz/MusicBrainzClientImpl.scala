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

package common.musicbrainz

import com.ning.http.client.Realm.{AuthScheme, RealmBuilder}
import com.typesafe.scalalogging.StrictLogging
import common.configuration.User
import dispatch.Defaults._
import dispatch._

import scala.xml.Elem

/**
 * The Class MusicBrainzClientImpl.
 *
 * @author alex
 */
class MusicBrainzClientImpl(val musicBrainzHost: String) extends MusicBrainzClient with StrictLogging {

  case class Collection(id: String, name: String, size: Int)

  override def relasesForOwner(user: User): Future[Traversable[String]] = {
    implicit val implicitUser = user
    for {
      collection <- defaultCollection
      ids <- loadCollection(collection)
    } yield ids
  }

  def defaultCollection(implicit user: User): Future[Collection] = {
    def collectionsResponse = Http(request.GET OK as.xml.Elem)
    collectionsResponse map findDefaultCollection
  }

  def findDefaultCollection(implicit user: User): Elem => Collection = (xml: Elem) => {
    val collections = for (collection <- xml \\ "collection") yield
      Collection(collection \@ "id", (collection \ "name").text, (collection \ "release-list" \@ "count").toInt)
    collections.find(_.name == ALL_MY_MUSIC) getOrElse {
      throw new IllegalStateException(s"User ${user.name} does not have a collection called $ALL_MY_MUSIC")
    }
  }

  def loadCollection(collection: Collection)(implicit user: User): Future[Traversable[String]] = {
    val eventualParts = 0 until collection.size by LIMIT map { offset => loadCollection(collection, offset)}
    Future.sequence(eventualParts).map(_.flatten)
  }

  def loadCollection(collection: Collection, offset: Int)(implicit user: User): Future[Traversable[String]] = {
    def collectionResponse =
      Http((request / collection.id / "releases").GET.<<?(Seq("offset" -> offset.toString, "limit" -> LIMIT.toString)) OK as.xml.Elem)
    collectionResponse map (releases => (releases \\ "release") map (_ \@ "id"))
  }

  /**
   * Add releases to an owner's collection.
   * @param user The user whose collection needs changing.
   * @param newReleaseIds The new releases to add to the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  override def addReleases(user: User, newReleaseIds: Set[String]): Future[Unit] = {
    alterReleases(request(user).PUT, newReleaseIds)(user)
  }

  /**
   * Remove releases from an owner's collection.
   * @param user The user whose collection needs changing.
   * @param oldReleaseIds The old releases to remove from the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  override def removeReleases(user: User, oldReleaseIds: Set[String]): Future[Unit] = {
    alterReleases(request(user).DELETE, oldReleaseIds)(user)
  }

  def alterReleases(partialReq: Req, releaseIds: Set[String])(implicit user: User): Future[Unit] = {
    val alterRelease: String => Future[Unit] = { collectionId =>
      val groups = releaseIds.toList.grouped(RELEASE_PATH_LIMIT)
      val eventualRequests = groups.map { releaseIds =>
        val req = partialReq / collectionId / "releases" / releaseIds.mkString(";")
        Http(req OK as.String)
      }
      Future.sequence(eventualRequests.toList) map { _ =>}
    }
    for {
      collection <- defaultCollection
      result <- alterRelease(collection.id)} yield result
  }

  def request(implicit user: User) = {
    host(musicBrainzHost).setRealm(
      new RealmBuilder()
        .setPrincipal(user.musicBrainzUserName)
        .setPassword(user.musicBrainzPassword)
        .setUsePreemptiveAuth(true)
        .setRealmName("musicbrainz.org")
        .setUseAbsoluteURI(false)
        .setScheme(AuthScheme.DIGEST)
        .build())
      .addHeader("User-Agent", USER_AGENT)
      .addHeader("Accept", "application/xml")
      .<<?(Seq(ID_PARAMETER)) / "ws" / "2" / "collection"
  }
}