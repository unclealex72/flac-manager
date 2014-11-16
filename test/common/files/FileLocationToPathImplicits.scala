package common.files

import java.nio.file.Path

/**
 * Allow FileLocations to be case to Paths in tests
 * Created by alex on 16/11/14.
 */
object FileLocationToPathImplicits {

  implicit val fileLocationToPath: FileLocation => Path = fl => fl.toPath
}
