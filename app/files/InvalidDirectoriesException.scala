package files

import java.nio.file.Path

/**
 * Created by alex on 23/10/14.
 */
class InvalidDirectoriesException(message: String, val paths: Traversable[Path]) extends Exception(message)
