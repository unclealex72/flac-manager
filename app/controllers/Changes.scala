package controllers

import common.changes.ChangeDao
import common.configuration.Users
import common.joda.JodaDateTime
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by alex on 14/12/14.
 */
class Changes(val users: Users, val changeDao: ChangeDao) extends Controller {

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

  def changelog(username: String, page: Int, limit: Int) = Action { implicit request =>
    users().find(_.name == username) match {
      case Some(user) => {
        val changelogCount = changeDao.countChangelog(user)
        val changelog = changeDao.changelog(user, page, limit)
        Ok(
          Json.obj(
            "total" -> changelogCount,
            "changelog" -> changelog.map { changelogItem =>
              Json.obj(
                "parentRelativePath" -> changelogItem.parentRelativePath,
                "at" -> JodaDateTime.format(changelogItem.at),
                "relativePath" -> changelogItem.relativePath
              )
            }
          )
        )
      }
      case _ => NotFound
    }
  }

}
