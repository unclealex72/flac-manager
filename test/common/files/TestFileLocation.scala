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

package common.files

import java.nio.file.{Path, Paths}

/**
 * A `FileLocation` that can be used for testing.
 * Created by alex on 02/11/14.
 */
case class TestFileLocation(val basePath: Path, val relativePath: Path, val readOnly: Boolean) extends FileLocation

object TestFileLocation {

  def apply(basePath: Path, readOnly: Boolean, path: String, paths: String*): TestFileLocation =
    TestFileLocation(basePath, Paths.get(path, paths: _*), readOnly)

  def apply(basePath: Path, path: String, paths: String*): TestFileLocation =
    TestFileLocation(basePath, true, path, paths: _*)
}