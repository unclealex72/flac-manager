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

import javax.inject.{Inject, Singleton}

import common.changes.ChangeDao
import common.configuration.{User, Users}
import common.joda.JodaDateTime
import org.joda.time.DateTime
import play.api.libs.json.{JsNumber, JsValue, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created by alex on 14/12/14.
 */
@Singleton
class Changes @Inject()(val users: Users, val changeDao: ChangeDao) extends Controller {

  def since(username: String, sinceStr: String)(jsonBuilder: User => DateTime => JsValue) = Action { implicit request =>
    val parameters = for {
      user <- users().find(_.name == username)
      since <- JodaDateTime(sinceStr)
    } yield (user, since)
    parameters match {
      case Some((user, since)) => {
        Ok(jsonBuilder(user)(since))
      }
      case _ => NotFound
    }
  }

  def changes(username: String, sinceStr: String) = since(username, sinceStr) { user => since =>
    val changes = changeDao.getAllChangesSince(user, since).map { change =>
      Json.obj(
        "action" -> change.action,
        "relativePath" -> change.relativePath,
        "at" -> JodaDateTime.format(change.at)
      )
    }
    Json.obj("changes" -> changes)
  }

  def countChangelog(username: String, sinceStr: String) = since(username, sinceStr) { user => since =>
    jsCount(changeDao.countChangelogSince(user, since))
  }

  def countChanges(username: String, sinceStr: String) = since(username, sinceStr) { user => since =>
    jsCount(changeDao.countChangesSince(user, since))
  }

  def jsCount(count: Long) = Json.obj("count" -> count)

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
