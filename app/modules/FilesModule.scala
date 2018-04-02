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

package modules

import java.nio.file.{FileSystems, FileSystem => JFileSystem}

import com.google.inject.AbstractModule
import common.files._
import common.music.{JaudioTaggerTagsService, TagsService}
import net.codingwell.scalaguice.ScalaModule
/**
  * Dependency injection for file related classes.
  */
class FilesModule  extends AbstractModule with ScalaModule {

  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind[FileSystem].annotatedWithName("rawFileSystem").to[FileSystemImpl].asEagerSingleton()
    bind[FileSystem].to[ProtectionAwareFileSystem].asEagerSingleton()
    bind[Repositories].to[RepositoriesImpl].asEagerSingleton()
    bind[TagsService].to[JaudioTaggerTagsService].asEagerSingleton()
    bind[FlacFileChecker].to[FlacFileCheckerImpl].asEagerSingleton()
    bind[JFileSystem].toInstance(FileSystems.getDefault)
  }
}

