package controllers

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import common.changes.ChangeDao
import common.configuration.{Directories, Users}
import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.joda.JodaDateTime
import common.music.{Tags, TagsService}
import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Result}
import play.utils.UriEncoding

/**
 * Created by alex on 18/11/14.
 */
class Music(val users: Users, val changeDao: ChangeDao)(implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions, val tagsService: TagsService) extends Controller {

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

  def changes(username: String, sinceStr: String) = Action { implicit request =>
    val parameters = for {
      user <- users().find(_.name == username)
      since <- JodaDateTime(sinceStr)
    } yield (user, since)
    parameters match {
      case Some((user, since)) => {
        val changes = changeDao.getAllChangesSince(user, since).map { change =>
          Json.obj(
            "action" -> change.action,
            "relativePath" -> change.relativePath,
            "at" -> JodaDateTime.format(change.at)
          )
        }
        Ok(Json.obj("changes" -> changes))
      }
      case _ => NotFound
    }
  }
}
