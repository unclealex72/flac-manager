package controllers

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import common.changes.ChangeDao
import common.configuration.{Directories, Users}
import common.files.{DeviceFileLocation, FileLocationExtensions}
import common.joda.JodaDateTime
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.utils.UriEncoding

/**
 * Created by alex on 18/11/14.
 */
class Music(val users: Users, val changeDao: ChangeDao)(implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions) extends Controller {

  def at(username: String, path: String) = Action { implicit request =>
    val decodedPath = UriEncoding.decodePath(path, StandardCharsets.UTF_8.toString)
    val musicFile = for {
      user <- users().find(_.name == username)
      musicFile <- DeviceFileLocation(user, Paths.get(decodedPath)).toFile
    } yield musicFile
    musicFile match {
      case Some(file) => Ok.chunked(Enumerator.fromFile(file))
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
