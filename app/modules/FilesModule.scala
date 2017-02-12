package modules

import com.google.inject.AbstractModule
import common.files._
import common.music.{JaudioTaggerTagsService, TagsService}
import net.codingwell.scalaguice.ScalaModule

class FilesModule  extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[FileLocationExtensions].to[FileLocationExtensionsImpl].asEagerSingleton()
    bind[FileSystem].annotatedWithName("rawFileSystem").to[FileSystemImpl].asEagerSingleton()
    bind[FileSystem].to[ProtectionAwareFileSystem].asEagerSingleton()
    bind[DirectoryService].to[DirectoryServiceImpl].asEagerSingleton()
    bind[DirectoryMappingService].to[NoOpDirectoryMappingService].asEagerSingleton()
    bind[TagsService].to[JaudioTaggerTagsService].asEagerSingleton()
    bind[FlacFileChecker].to[FlacFileCheckerImpl].asEagerSingleton()
  }
}

