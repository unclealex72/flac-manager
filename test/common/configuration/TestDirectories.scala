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

package common.configuration

import java.nio.file.{FileSystem, Path}

/**
 * A common.configuration interface that is used to hold where the various directories are.
 * @author alex
 *
 */
case class TestDirectories(
                            /**
                             * Gets the top level path where temporary files are created.
                             *
                             * @return the top level path where temporary files are created
                             */
                            temporaryPath: Path,
                            /**
                              * Gets the file that is used by clients to work out what relative directory a file is in
                              * @return
                              */
                            datumPath: Path
                          ) extends Directories

object TestDirectories {

  def apply(fs: FileSystem,
             temp: String = "/tmp",
             datum: String = "/datum"): TestDirectories = {
    TestDirectories(fs.getPath(temp), fs.getPath(datum))
  }
}