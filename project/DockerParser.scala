import java.nio.file.{Files, Path, Paths}

import com.typesafe.sbt.packager.docker.Cmd
import scala.collection.JavaConverters._

/*
 * Copyright 2017 Alex Jones
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

/**
  * Parse a docker file and turn it into a list of docker commands
  * Created by alex on 23/07/17
  **/
object DockerParser {

  private case class MultiLineAccumulator(lines: Seq[String] = Seq.empty, continueLast: Boolean = false) {
  }

  def apply(dockerFilename: String): Seq[Cmd] = {
    val dockerPath: Path = Paths.get("project", dockerFilename)
    val dockerContents: Seq[String] =
      Files.readAllLines(dockerPath).asScala.map(_.trim).filterNot(_.isEmpty).filterNot(_.startsWith("#"))
    val dockerMultilineAccumulator: MultiLineAccumulator =
      dockerContents.foldLeft(MultiLineAccumulator()) { (acc, line) =>
      val (strippedLine, continueNext) = {
        if (line.endsWith("\\")) {
          (line.substring(0, line.length - 1).trim, true)
        }
        else {
          (line, false)
        }
      }
      if (acc.continueLast) {
        val reversedLines = acc.lines.reverse
        val lastLine = reversedLines.head + " " + strippedLine
        MultiLineAccumulator(reversedLines.tail.reverse :+ lastLine, continueNext)
      }
      else {
        MultiLineAccumulator(acc.lines :+ strippedLine, continueNext)
      }
    }
    val dockerCommandLines = dockerMultilineAccumulator.lines
    dockerCommandLines.map { dockerCommandLine =>
      val commandParts = dockerCommandLine.split("""\s+""")
      Cmd(commandParts.head, commandParts.tail :_*)
    }
  }
}
