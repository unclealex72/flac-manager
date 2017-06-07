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
import java.time.{Instant, ZoneId, ZonedDateTime}
import javax.inject.{Inject, Singleton}

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.configuration.{Directories, User, UserDao}
import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.music.{Tags, TagsService}
import play.api.mvc._
import play.utils.UriEncoding

import scala.util.Try

/**
  * A controller that streams music information, namely: tags, artwork and the music itself.
  * @param userDao The [[UserDao]] used to list users.
  * @param directories The [[Directories]] containing the location of the repositories.
  * @param fileLocationExtensions The typeclass used to give [[Path]]-like functionality to
  *                               [[common.files.FileLocation]]s.
  * @param tagsService The [[TagsService]] used to read an audio file's tags.
  */
@Singleton
class Music @Inject()(val userDao: UserDao, val controllerComponents: ControllerComponents)(
  implicit val directories: Directories,
  val fileLocationExtensions: FileLocationExtensions,
  val tagsService: TagsService, val commandExecutionContext: CommandExecutionContext) extends BaseController with StrictLogging {

  /**
    * Stream an audio file
    * @param username The name of the user who owns the track.
    * @param path The relative path of the track.
    * @return An MP3 stream of music from a user's device repository.
    */
  def music(username: String, path: String): Action[AnyContent] = musicFile("music", username, path, deviceFileAt) {
    deviceFileLocation =>
      Ok.sendFile(deviceFileLocation.toFile).as("audio/mpeg")
  }

  /**
    * Stream an audio file
    * @param username The name of the user who owns the track.
    * @param path The relative path of the track.
    * @return An MP3 stream of music from a user's device repository.
    */
  def tags(username: String, path: String): Action[AnyContent] =
    serveTags("tags", username, path, deviceFileAt)(tags => Ok(tags.toJson(false)))

  /**
    * Serve a response based on an audio file's tags.
    * @param username The name of the user who owns the file.
    * @param path The path at which the file is located.
    * @param deviceFileLocator A function that gets the device file given a user and path.
    * @param responseBuilder A function to build the response given the calculated tags.
    * @return The result of the `responseBuilder` or 404 if the file or user could not be found
    */
  def serveTags(requestType: String,
                username: String,
                path: String,
                deviceFileLocator: (User, Path) => Option[DeviceFileLocation])
               (responseBuilder: Tags => Result): Action[AnyContent] = musicFile(requestType, username, path, deviceFileLocator) { deviceFileLocation =>
    deviceFileLocation.toFlacFileLocation.readTags match {
      case Invalid(_) =>
        NotFound
      case Valid(tags) =>
        responseBuilder(tags)
    }
  }

  /**
    * Serve a response based on a [[common.files.FileLocation]].
    * @param username The name of the user who owns the file.
    * @param path The path at which the file is located.
    * @param deviceFileLocator A function that gets the device file given a user and path.
    * @param resultBuilder A function to build the response from the file calculated file location.
    * @return The result of the `responseBuilder` or 404 if the file or user could not be found
    */
  def musicFile(
                requestType: String,
                username: String,
                path: String,
                deviceFileLocator: (User, Path) => Option[DeviceFileLocation])
               (resultBuilder: DeviceFileLocation => Result) = Action { implicit request: Request[AnyContent] =>
    logger.info(s"Received a request for $requestType for $username at $path")
    val decodedPath = UriEncoding.decodePath(path, StandardCharsets.UTF_8.toString).replace('+', ' ')
    val musicFile = for {
      user <- userDao.allUsers().find(_.name == username)
      musicFile <- deviceFileLocator(user, Paths.get(decodedPath))
    } yield musicFile
    musicFile match {
      case Some(deviceFileLocation) =>
        val lastModified: Instant = deviceFileLocation.lastModified
        val maybeNotModified = for {
          ifModifiedSinceValue <-
            request.headers.get("If-Modified-Since")
          ifModifiedSinceDate <-
            Try(ZonedDateTime.parse(ifModifiedSinceValue, ResponseHeader.httpDateFormat)).toOption
            if lastModified.isBefore(ifModifiedSinceDate.toInstant)
        } yield NotModified
        maybeNotModified.getOrElse(resultBuilder(deviceFileLocation)).
          withDateHeaders("Last-Modified" -> ZonedDateTime.ofInstant(lastModified, ZoneId.systemDefault()))
      case _ => NotFound
    }
  }

  /**
    * Get the album artwork for a track.
    * @param username The name of the user who owns the track.
    * @param path The path of the track relative to the user's device repository.
    * @return The album artwork or 404 if the track or user could not be found.
    */
  def artwork(username: String, path: String): Action[AnyContent] = serveTags("artwork", username, path, firstDeviceFileIn) { tags =>
    val coverArt = tags.coverArt
    Ok(coverArt.imageData).as(coverArt.mimeType)
  }

  /**
    * A method that can be used to find a file by directly looking a file in a user's device repository.
    * @param user The user who owns the property.
    * @param path The path of the file relative to the user's device repository.
    * @return A [[DeviceFileLocation]] if one exists, none otherwise.
    */
  def deviceFileAt(user: User, path: Path): Option[DeviceFileLocation] =
    DeviceFileLocation(user, path).ifExists

  /**
    * A method that can be used to find a file by looking for the first file in directory in a user's device repository.
    * This is to allow album artwork to be correctly cached by clients.
    * @param user The user who owns the property.
    * @param parentPath The path of the album relative to the user's device repository.
    * @return A [[DeviceFileLocation]] if one exists, none otherwise.
    */
  def firstDeviceFileIn(user: User, parentPath: Path): Option[DeviceFileLocation] =
    DeviceFileLocation(user, parentPath).firstInDirectory

}
