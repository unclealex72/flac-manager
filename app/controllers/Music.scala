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
import java.nio.file.Paths

import common.configuration.{Directories, Users}
import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.music.{Tags, TagsService}
import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{Action, Controller, Result}
import play.utils.UriEncoding

/**
 * Created by alex on 18/11/14.
 */
class Music(val users: Users)(implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions, val tagsService: TagsService) extends Controller {

  def at(username: String, path: String) = musicFile(username, path) { deviceFileLocation =>
    Ok.chunked(Enumerator.fromFile(deviceFileLocation.toFile))
  }

  def serveTags(username: String, path: String)(responseBuilder: Tags => Result) = musicFile(username, path) { deviceFileLocation =>
    deviceFileLocation.toFlacFileLocation.readTags match {
      case Left(violations) => {
        NotFound
      }
      case Right(tags) =>
        responseBuilder(tags)
    }
  }

  def tags(username: String, path: String) = serveTags(username, path)(tags => Ok(tags.toJson(false)))

  def artwork(username: String, path: String) = serveTags(username, path) { tags =>
    val coverArt = tags.coverArt
    Ok(coverArt.imageData).withHeaders(HeaderNames.CONTENT_TYPE -> coverArt.mimeType)
  }

  def musicFile(username: String, path: String)(resultBuilder: DeviceFileLocation => Result) = Action { implicit request =>
    val decodedPath = UriEncoding.decodePath(path, StandardCharsets.UTF_8.toString)
    val musicFile = for {
      user <- users().find(_.name == username)
      musicFile <- DeviceFileLocation(user, Paths.get(decodedPath)).ifExists
    } yield musicFile
    musicFile match {
      case Some(deviceFileLocation) => resultBuilder(deviceFileLocation)
      case _ => NotFound
    }
  }
}
