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

import java.nio.file.{Path, Paths}
import javax.inject.{Inject, Singleton}

import common.changes.ChangeDao
import common.configuration.{User, Users}
import common.joda.JodaDateTime
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, Call, Controller, RequestHeader}

/**
  * Created by alex on 14/12/14.
  */
@Singleton
class Changes @Inject()(val users: Users, val changeDao: ChangeDao) extends Controller {

  def since(username: String, sinceStr: String)(jsonBuilder: RequestHeader => User => DateTime => JsValue) = Action { implicit request =>
    val parameters = for {
      user <- users().find(_.name == username)
      since <- JodaDateTime(sinceStr)
    } yield (user, since)
    parameters match {
      case Some((user, since)) => {
        Ok(jsonBuilder(request)(user)(since))
      }
      case _ => NotFound
    }
  }

  def changes(username: String, sinceStr: String) = since(username, sinceStr) { implicit request => {
    user => since =>
      val changes = changeDao.getAllChangesSince(user, since)
      val jsonChanges = changes.map { change =>
        val changeObj = Json.obj(
          "action" -> change.action,
          "relativePath" -> change.relativePath,
          "at" -> JodaDateTime.format(change.at)
        )
        if (change.action == "added") {
          changeObj ++ links(username, change.relativePath)
        }
        else {
          changeObj
        }
      }.distinct
      Json.obj("changes" -> jsonChanges)
    }
  }

  def links(user: String, relativePath: String)(implicit request: RequestHeader): JsObject = {
    def url(callBuilder: (String, String) => Call, path: Path): String =
      callBuilder(user, path.toString).absoluteURL().replace(' ', '+')

    val path = Paths.get(relativePath)
    Json.obj(
      "_links" -> Json.obj(
        "music" -> url(routes.Music.music, path),
        "tags" -> url(routes.Music.tags, path),
        "artwork" -> url(routes.Music.artwork, path.getParent)
      )
    )
  }

  def changelog(username: String, sinceStr: String) = since(username, sinceStr) { implicit request => {
    user => since =>
      val changelogCount = changeDao.countChangelog(user)
      val changelog = changeDao.changelog(user, since)
      Json.obj(
        "total" -> changelogCount,
        "changelog" -> changelog.map { changelogItem =>
          Json.obj(
            "parentRelativePath" -> changelogItem.parentRelativePath,
            "at" -> JodaDateTime.format(changelogItem.at),
            "relativePath" -> changelogItem.relativePath
          ) ++ links(username, changelogItem.relativePath)
        }
      )
    }
  }
}