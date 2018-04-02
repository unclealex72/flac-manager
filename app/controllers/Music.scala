/*
 * Copyright 2018 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import java.nio.charset.StandardCharsets
import java.nio.file.{FileSystem, Path}
import java.time.{Instant, ZoneId, ZonedDateTime}

import cats.data.Validated.{Invalid, Valid}
import com.typesafe.scalalogging.StrictLogging
import common.async.CommandExecutionContext
import common.configuration.{User, UserDao}
import common.files.{DeviceFile, Extension, Repositories}
import common.message.{MessageService, MessageServiceBuilder}
import common.music.{CoverArt, Tags}
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.utils.UriEncoding

import scala.util.Try

/**
  * A controller that streams music information, namely: tags, artwork and the music itself.
  * @param userDao The [[UserDao]] used to list users.
  * @param messageServiceBuilder The builder used to create message services.
  * @param repositories The locations of file repositories.
  * @param fs The underlying [[FileSystem]].
  */
@Singleton
class Music @Inject()(val userDao: UserDao,
                      val messageServiceBuilder: MessageServiceBuilder,
                      val controllerComponents: ControllerComponents,
                      val repositories: Repositories,
                      val fs: FileSystem)(
  implicit val commandExecutionContext: CommandExecutionContext) extends BaseController with StrictLogging {

  /**
    * Messages need only to be logged.
    */
  implicit val messageService: MessageService = messageServiceBuilder.build

  /**
    * Stream an audio file
    * @param user The user who owns the track.
    * @param extension The type of track.
    * @param path The relative path of the track.
    * @return An MP3 stream of music from a user's device repository.
    */
  def music(user: User, extension: Extension, path: String): Action[AnyContent] =
    musicFile("music", user, extension, path, deviceFileAt) {
      deviceFile => Ok.sendPath(deviceFile.absolutePath).as(deviceFile.extension.mimeType)
  }

  /**
    * Return an audio file's tags.
    * @param user The user who owns the track.
    * @param extension The type of track.
    * @param path The relative path of the track.
    * @return The tags for the audio file.
    */
  def tags(user: User, extension: Extension, path: String): Action[AnyContent] =
    serveTags("tags", user, extension, path, deviceFileAt)(tags => Ok(tags.toJson(false)))

  /**
    * Serve a response based on an audio file's tags.
    * @param user The user who owns the track.
    * @param extension The type of track.
    * @param path The relative path of the track.
    * @param deviceFileLocator A function that gets the device file given a user and path.
    * @param responseBuilder A function to build the response given the calculated tags.
    * @return The result of the `responseBuilder` or 404 if the file or user could not be found
    */
  def serveTags(requestType: String,
                user: User,
                extension: Extension,
                path: String,
                deviceFileLocator: (User, Extension, Path) => Option[DeviceFile])
               (responseBuilder: Tags => Result): Action[AnyContent] =
    musicFile(requestType, user, extension, path, deviceFileLocator) { deviceFile =>
    deviceFile.tags.read() match {
      case Invalid(_) =>
        NotFound("")
      case Valid(tags) =>
        responseBuilder(tags)
    }
  }

  /**
    * Serve a response based on a [[common.files.File]].
    * @param user The user who owns the track.
    * @param extension The type of track.
    * @param path The relative path of the track.
    * @param deviceFileLocator A function that gets the device file given a user and path.
    * @param resultBuilder A function to build the response from the file calculated file location.
    * @return The result of the `responseBuilder` or 404 if the file or user could not be found
    */
  def musicFile(
                requestType: String,
                user: User,
                extension: Extension,
                path: String,
                deviceFileLocator: (User, Extension, Path) => Option[DeviceFile])
               (resultBuilder: DeviceFile  => Result) = Action { implicit request: Request[AnyContent] =>
    val username: String = user.name
    logger.info(s"Received a request for $requestType for $username at $path")
    val decodedPath: String = UriEncoding.decodePath(path, StandardCharsets.UTF_8.toString).replace('+', ' ')
    val musicFiles: Set[DeviceFile] = for {
      user <- userDao.allUsers() if user.name == username
      musicFile <- deviceFileLocator(user, extension, fs.getPath(decodedPath))
    } yield musicFile
    musicFiles.headOption match {
      case Some(deviceFile) =>
        val lastModified: Instant = deviceFile.lastModified
        val maybeNotModified: Option[Result] = for {
          ifModifiedSinceValue <-
            request.headers.get("If-Modified-Since")
          ifModifiedSinceDate <-
            Try(ZonedDateTime.parse(ifModifiedSinceValue, ResponseHeader.httpDateFormat)).toOption
            if lastModified.isBefore(ifModifiedSinceDate.toInstant)
        } yield NotModified
        maybeNotModified.getOrElse(resultBuilder(deviceFile)).
          withDateHeaders("Last-Modified" -> ZonedDateTime.ofInstant(lastModified, ZoneId.systemDefault()))
      case _ => NotFound
    }
  }

  /**
    * Get the album artwork for a track.
    * @param user The user who owns the track.
    * @param extension The type of track.
    * @param path The relative path of the track.
    * @return The album artwork or 404 if the track or user could not be found.
    */
  def artwork(user: User, extension: Extension, path: String): Action[AnyContent] =
    serveTags("artwork", user, extension, path, firstDeviceFileIn) { tags =>
    val coverArt: CoverArt = tags.coverArt
    Ok(coverArt.imageData).as(coverArt.mimeType)
  }

  /**
    * A method that can be used to find a file by directly looking a file in a user's device repository.
    * @param user The user who owns the property.
    * @param extension The type of lossy file.
    * @param path The path of the file relative to the user's device repository.
    * @return A [[DeviceFile]] if one exists, none otherwise.
    */
  def deviceFileAt(user: User, extension: Extension, path: Path): Option[DeviceFile] = {
    repositories.device(user, extension).file(path).toOption.filter(_.exists)
  }

  /**
    * A method that can be used to find a file by looking for the first file in directory in a user's device repository.
    * This is to allow album artwork to be correctly cached by clients.
    * @param user The user who owns the property.
    * @param parentPath The path of the album relative to the user's device repository.
    * @return A [[DeviceFile]] if one exists, none otherwise.
    */
  def firstDeviceFileIn(user: User, extension: Extension, parentPath: Path): Option[DeviceFile] = {
    repositories.device(user, extension).directory(parentPath).toOption.flatMap(_.list(1).headOption)
  }

}
