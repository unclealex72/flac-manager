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

package controllers

import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}
import javax.inject.{Inject, Singleton}

import cats.data.Validated.{Invalid, Valid}
import common.configuration.{Directories, User, Users}
import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.music.{Tags, TagsService}
import org.joda.time.DateTime
import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.utils.UriEncoding

import scala.util.Try

/**
 * Created by alex on 18/11/14.
 */
@Singleton
class Music @Inject()(val users: Users)(implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions, val tagsService: TagsService) extends Controller {

  def music(username: String, path: String) = musicFile(username, path, deviceFileAt) { deviceFileLocation =>
    Ok.chunked(Enumerator.fromFile(deviceFileLocation.toFile))
  }

  def tags(username: String, path: String) = serveTags(username, path, deviceFileAt)(tags => Ok(tags.toJson(false)))

  def serveTags(username: String,
                path: String,
                deviceFileLocator: (User, Path) => Option[DeviceFileLocation])
               (responseBuilder: Tags => Result) = musicFile(username, path, deviceFileLocator) { deviceFileLocation =>
    deviceFileLocation.toFlacFileLocation.readTags match {
      case Invalid(_) =>
        NotFound
      case Valid(tags) =>
        responseBuilder(tags)
    }
  }

  def musicFile(username: String,
                path: String,
                deviceFileLocator: (User, Path) => Option[DeviceFileLocation])
               (resultBuilder: DeviceFileLocation => Result) = Action { implicit request =>
    val decodedPath = UriEncoding.decodePath(path, StandardCharsets.UTF_8.toString).replace('+', ' ')
    val musicFile = for {
      user <- users().find(_.name == username)
      musicFile <- deviceFileLocator(user, Paths.get(decodedPath))
    } yield musicFile
    musicFile match {
      case Some(deviceFileLocation) => {
        val lastModified = new DateTime(deviceFileLocation.lastModified)
        val maybeNotModified = for {
          ifModifiedSinceValue <-
            request.headers.get("If-Modified-Since")
          ifModifiedSinceDate <-
            Try(ResponseHeader.httpDateFormat.parseDateTime(ifModifiedSinceValue)).toOption
            if lastModified.isBefore(ifModifiedSinceDate)
        } yield NotModified
        maybeNotModified.getOrElse(resultBuilder(deviceFileLocation)).withDateHeaders("Last-Modified" -> lastModified)
      }
      case _ => NotFound
    }
  }

  def artwork(username: String, path: String) = serveTags(username, path, firstDeviceFileIn) { tags =>
    val coverArt = tags.coverArt
    Ok(coverArt.imageData).withHeaders(HeaderNames.CONTENT_TYPE -> coverArt.mimeType)
  }

  def deviceFileAt(user: User, path: Path): Option[DeviceFileLocation] =
    DeviceFileLocation(user, path).ifExists

  def firstDeviceFileIn(user: User, parentPath: Path): Option[DeviceFileLocation] =
    DeviceFileLocation(user, parentPath).firstInDirectory

}
