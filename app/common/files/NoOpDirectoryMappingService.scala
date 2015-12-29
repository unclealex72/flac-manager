package common.files

import java.nio.file.{Paths, Path}

/**
  * A {@link DirectoryMappingService} that just transforms strings straight in to paths.
  * Created by alex on 27/12/15.
  */
class NoOpDirectoryMappingService extends DirectoryMappingService {
  /**
    * Resolve a set of client side directories into a map of server side directories.
    * @param mtab The contents of the client's /etc/mtab file.
    * @param directories
    * @return A map of the original directories to the resolved directories
    */
  override def withMtab(mtab: String): (String) => Path = str => Paths.get(str)
}
