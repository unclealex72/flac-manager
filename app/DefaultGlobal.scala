/*
 * Copyright 2017 Alex Jones
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

import akka.actor.ActorSystem
import checkin._
import checkin.actors.{CheckinActor, EncodingActor}
import checkout.{CheckoutCommand, CheckoutCommandImpl, CheckoutService, CheckoutServiceImpl}
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.scalalogging.LazyLogging
import common.changes.{ChangeDao, SquerylChangeDao}
import common.collections.{CollectionDao, SquerylCollectionDao}
import common.commands.{CommandService, TempFileCommandService}
import common.configuration._
import common.files._
import common.message.{I18nMessageServiceBuilder, MessageServiceBuilder}
import common.music.{JaudioTaggerTagsService, TagsService}
import common.now.{NowService, NowServiceImpl}
import common.owners.{OwnerService, OwnerServiceImpl}
import controllers.{ParameterBuilders, ParameterBuildersImpl}
import initialise.{InitialiseCommand, InitialiseCommandImpl}
import net.codingwell.scalaguice.ScalaModule
import org.squeryl.adapters.{H2Adapter, MySQLInnoDBAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import own.{OwnCommand, OwnCommandImpl}
import play.api.db.DB
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}
import sync._

/**
  * Created by alex on 12/02/17
  **/
trait DefaultGlobal extends GlobalSettings with LazyLogging {

  override def onStart(app: Application) {
    super.onStart(app)
    logger info "Setting up database access."
    // Set up Squeryl database access
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("com.mysql.jdbc.Driver") => Some(() => {
        val adapter = new MySQLInnoDBAdapter {
          override def quoteIdentifier(s: String) = "`" + s + "`"
        }
        getSession(adapter, app)
      })
      case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver")
    }
  }

  def getSession(adapter: DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)

}
