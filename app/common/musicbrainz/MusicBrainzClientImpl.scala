/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.musicbrainz

import java.io.InputStream
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import javax.ws.rs.core.{HttpHeaders, MediaType, MultivaluedMap}
import javax.ws.rs.ext.MessageBodyReader

import com.sun.jersey.api.client.filter.ClientFilter
import com.sun.jersey.api.client.{ClientRequest, ClientResponse, WebResource}
import com.sun.jersey.client.apache.ApacheHttpClient
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig._
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig
import com.typesafe.scalalogging.StrictLogging
import common.configuration.{PlayConfiguration, User}
import play.api.Configuration

import scala.xml.{Elem, XML}

/**
 * The Class MusicBrainzClientImpl.
 *
 * @author alex
 */
class MusicBrainzClientImpl(val musicBrainzHost: String) extends MusicBrainzClient with StrictLogging {

  case class Collection(id: String, name: String, size: Int)

  override def relasesForOwner(user: User): Traversable[String] = {
    implicit val implicitUser = user
    loadCollection(defaultCollection)
  }

  def defaultCollection(implicit user: User): Collection = {
    def collectionsResponse = MusicBrainzWebResource(user).get(classOf[Elem])
    findDefaultCollection(user)(collectionsResponse)
  }

  def findDefaultCollection(implicit user: User): Elem => Collection = (xml: Elem) => {
    logger.debug(s"Looking for default connection\n${xml}")
    val collections = for {
      collection <- xml \\ "collection"
      releaseList <- collection \ "release-list"
    } yield
      Collection(collection \@ "id", (collection \ "name").text, (releaseList \@ "count").toInt)
    collections.find(_.name == ALL_MY_MUSIC) getOrElse {
      throw new IllegalStateException(s"User ${user.name} does not have a collection called $ALL_MY_MUSIC")
    }
  }

  def loadCollection(collection: Collection)(implicit user: User): Traversable[String] = {
    val parts = 0 until collection.size by LIMIT map { offset => loadCollection(collection, offset)}
    parts.flatten
  }

  def loadCollection(collection: Collection, offset: Int)(implicit user: User): Traversable[String] = {
    val webResource = MusicBrainzWebResource(user)
    val collectionResponse =
      webResource.path(collection.id).path("releases").queryParam("offset", offset.toString).queryParam("limit", LIMIT.toString).get(classOf[Elem])
    collectionResponse \\ "release" map (_ \@ "id")
  }

  /**
   * Add releases to an owner's collection.
   * @param user The user whose collection needs changing.
   * @param newReleaseIds The new releases to add to the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  override def addReleases(user: User, newReleaseIds: Set[String]): Unit = {
    alterReleases(wr => cl => wr.put(cl), newReleaseIds)(user)
  }

  /**
   * Remove releases from an owner's collection.
   * @param user The user whose collection needs changing.
   * @param oldReleaseIds The old releases to remove from the user's collection.
   * @throws thrown if a unique collection cannot be found.
   */
  override def removeReleases(user: User, oldReleaseIds: Set[String]): Unit = {
    alterReleases(wr => cl => wr.delete(cl), oldReleaseIds)(user)
  }

  def alterReleases(method: WebResource => Class[String] => String, releaseIds: Set[String])(implicit user: User): Unit = {
    val alterRelease: String => Unit = { collectionId =>
      val groups = releaseIds.toList.grouped(RELEASE_PATH_LIMIT)
      groups.foreach { releaseIds =>
        val webResource = MusicBrainzWebResource(user).path(collectionId).path("releases").path(releaseIds.mkString(";"))
        method(webResource)(classOf[String])
      }
    }
    alterRelease(defaultCollection.id)
  }

  object MusicBrainzWebResource extends MessageBodyReader[Elem] {

    override def isReadable(`type`: Class[_], genericType: Type, annotations: Array[Annotation], mediaType: MediaType): Boolean = {
      classOf[Elem] == `type`
    }

    override def readFrom(
                           `type`: Class[Elem],
                           genericType: Type,
                           annotations: Array[Annotation],
                           mediaType: MediaType,
                           httpHeaders: MultivaluedMap[String, String],
                           entityStream: InputStream): Elem = XML.load(entityStream)

    def apply(user: User): WebResource = {
      val cc = new DefaultApacheHttpClientConfig()
      cc.getSingletons().add(this)
      cc.getProperties().put(PROPERTY_PREEMPTIVE_AUTHENTICATION, java.lang.Boolean.TRUE)
      cc.getState().setCredentials("musicbrainz.org", null, -1, user.musicBrainzUserName, user.musicBrainzPassword)
      val client = ApacheHttpClient.create(cc)
      client.addFilter(new MusicBrainzRetryFilter(100, 5))
      val userAgentClientFilter = new ClientFilter() {
        override def handle(cr: ClientRequest): ClientResponse = {
          cr.getHeaders().add(HttpHeaders.USER_AGENT, USER_AGENT)
          getNext().handle(cr)
        }
      }
      client.addFilter(userAgentClientFilter)
      client.resource(s"http://${musicBrainzHost}/ws/2/collection")
    }

  }

}

class PlayConfigurationMusicBrainzClient(override val configuration: Configuration) extends PlayConfiguration[MusicBrainzClient](configuration) with MusicBrainzClient {

  override def load(configuration: Configuration): Option[MusicBrainzClient] =
    configuration.getString("musicbrainzHost").map(host => new MusicBrainzClientImpl(host))

  override def addReleases(user: User, newReleaseIds: Set[String]): Unit = result.addReleases(user, newReleaseIds)

  override def removeReleases(user: User, oldReleaseIds: Set[String]): Unit = result.removeReleases(user, oldReleaseIds)

  override def relasesForOwner(user: User): Traversable[String] = result.relasesForOwner(user)
}