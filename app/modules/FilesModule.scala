package modules

import com.google.inject.AbstractModule
import common.files._
import common.music.{JaudioTaggerTagsService, TagsService}
import net.codingwell.scalaguice.ScalaModule
import java.nio.file.{FileSystems, FileSystem => JFileSystem}
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

