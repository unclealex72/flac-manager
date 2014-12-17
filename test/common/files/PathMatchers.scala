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

import java.nio.file.{Files, Path}

import org.specs2.matcher.{Expectable, Matcher}

/**
 * Specs2 matchers for Paths.
 * Created by alex on 24/10/14.
 */
trait PathMatchers {

  val exist = new Matcher[Path] {
    def apply[S <: Path](s: Expectable[S]) = {
      result(Files.exists(s.value),
        s.description + " exists",
        s.description + " does not exist",
        s)
    }
  }

  val beADirectory = new Matcher[Path] {
    def apply[S <: Path](s: Expectable[S]) = {
      result(Files.isDirectory(s.value),
        s.description + " is a directory",
        s.description + " is not a directory",
        s)
    }
  }

  def beTheSameFileAs(otherPath: Path) = new Matcher[Path] {
    def apply[S <: Path](s: Expectable[S]) = {
      result(Files.isSameFile(s.value, otherPath),
        s.description + " is a the same path as " + otherPath,
        s.description + " is not the same path as " + otherPath,
        s)
    }
  }

  val beASymbolicLink = new Matcher[Path] {
    def apply[S <: Path](s: Expectable[S]) = {
      result(Files.isSymbolicLink(s.value),
        s.description + " is a symbolic link",
        s.description + " is not a symbolic link",
        s)
    }
  }

  val beAbsolute = new Matcher[Path] {
    def apply[S <: Path](s: Expectable[S]) = {
      result(s.value.isAbsolute,
        s.description + " is absolute",
        s.description + " is relative",
        s)
    }
  }
}
