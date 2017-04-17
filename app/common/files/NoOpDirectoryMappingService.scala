package common.files

import java.nio.file.{Path, Paths}
import javax.inject.Inject

/**
  * A {@link DirectoryMappingService} that just transforms strings straight in to paths.
  * Created by alex on 27/12/15.
  */
class NoOpDirectoryMappingService @Inject() extends DirectoryMappingService {
  /**
    * Resolve a set of client side directories into a map of server side directories.
    *
    * @param datumFileLocation The location of the datum file on the client.
    * @return A function that converts client paths to server paths
    */
  override def withDatumFileLocation(datumFileLocation: String): (String) => Path = Paths.get(_)
}
