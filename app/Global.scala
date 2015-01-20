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

import akka.actor.ActorSystem
import checkin._
import checkin.actors.{CheckinActor, EncodingActor}
import checkout.{CheckoutCommand, CheckoutCommandImpl, CheckoutService, CheckoutServiceImpl}
import com.typesafe.scalalogging.LazyLogging
import common.changes.{ChangeDao, SquerylChangeDao}
import common.commands.{CommandService, TempFileCommandService}
import common.configuration._
import common.files._
import common.message.{I18nMessageServiceBuilder, MessageServiceBuilder}
import common.music.{JaudioTaggerTagsService, TagsService}
import common.musicbrainz.{MusicBrainzClient, PlayConfigurationMusicBrainzClient}
import common.now.{NowService, NowServiceImpl}
import common.owners.{OwnerService, OwnerServiceImpl}
import controllers._
import initialise.{InitialiseCommand, InitialiseCommandImpl}
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import own.{OwnCommand, OwnCommandImpl}
import play.api.Play.current
import play.api.db.DB
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}
import scaldi.play.ScaldiSupport
import scaldi.{DynamicModule, Injector}
import sync._

/**
 * The [[GlobalSettings]] used to set up Squeryl and Subcut
 * @author alex
 *
 */

object Global extends DefaultGlobal

trait DefaultGlobal extends GlobalSettings with ScaldiSupport with LazyLogging {

  class ConfigurationModule extends DynamicModule {
    bind[Users] to injected[PlayConfigurationUsers]
    bind[Directories] to injected[PlayConfigurationDirectories]
    bind[Int] identifiedBy('numberOfConcurrentEncoders) to injected[PlayConfigurationNumberOfEncoders].numberOfEncoders

  }

  class CommonModule extends DynamicModule {
    bind[ChangeDao] to injected[SquerylChangeDao]
    bind[CommandService] to injected[TempFileCommandService]
    bind[OwnerService] to injected[OwnerServiceImpl]
    bind[NowService] to injected[NowServiceImpl]
  }

  class MusicBrainzModule extends DynamicModule {
    bind[MusicBrainzClient] to injected[PlayConfigurationMusicBrainzClient]

  }

  class FilesModule extends DynamicModule {
    bind[FileLocationExtensions] to injected[FileLocationExtensionsImpl]
    bind[FileSystem] identifiedBy 'rawFileSystem to injected[FileSystemImpl]
    bind[FileSystem] to ProtectionAwareFileSystem.injected('rawFileSystem)(this)
    bind[DirectoryService] to injected[DirectoryServiceImpl]
    bind[DirectoryMappingService] to injected[DirectoryMappingServiceImpl]
    bind[TagsService] to injected[JaudioTaggerTagsService]
    bind[FlacFileChecker] to injected[FlacFileCheckerImpl]

  }

  class MessagesModule extends DynamicModule {
    bind[MessageServiceBuilder] to I18nMessageServiceBuilder()
  }

  class CommandsModule extends DynamicModule {
    bind[SyncCommand] to injected[SyncCommandImpl]
    bind[CheckinCommand] to injected[CheckinCommandImpl]
    bind[CheckoutCommand] to injected[CheckoutCommandImpl]
    bind[OwnCommand] to injected[OwnCommandImpl]
    bind[InitialiseCommand] to injected[InitialiseCommandImpl]
  }

  class ControllersModule extends DynamicModule {
    bind[ParameterBuilders] to injected[ParameterBuildersImpl]
    bind[Music] to injected[Music]
    bind[Commands] to injected[Commands]
    bind[Conf] to injected[Conf]
    bind[Changes] to injected[Changes]
  }

  class SyncModule extends DynamicModule {
    bind[DeviceConnectionService] to injected[DeviceConnectionServiceImpl]
    bind[SynchronisationManager] to injected[SynchronisationManagerImpl]
  }

  class CheckinModule extends DynamicModule {
    bind[CheckinService] to new CheckinServiceImpl
    bind[Mp3Encoder] to injected[Mp3EncoderImpl]
    // Actors
    bind[ActorSystem] to Akka.system
    binding toProvider new CheckinActor
    binding toProvider new EncodingActor
  }

  class CheckoutModule extends DynamicModule {
    bind[CheckoutService] to injected[CheckoutServiceImpl]
  }

  override def applicationModule: Injector = new ConfigurationModule :: new CommonModule :: new MusicBrainzModule ::
    new FilesModule :: new MessagesModule :: new CommandsModule :: new ControllersModule :: new SyncModule ::
    new CheckinModule :: new CheckoutModule

  override def onStart(app: Application) {
    super.onStart(app)
    logger info "Setting up database access."
    // Set up Squeryl database access
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter, app))
      case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver")
    }
  }

  def getSession(adapter: DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)

}