import java.nio.file.{Files, Path, Paths}
import java.time.{Clock, Duration}

import common.files.Extension._
import common.music.JaudioTaggerTagsService

import scala.sys.process._
import scala.compat.java8.StreamConverters._
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
  * Created by alex on 26/06/17
  **/
object Tagger extends App {

  val directories: Seq[Path] = Seq(
    "M/Metallica/Load",
    "Q/Queen/A Night at the Opera",
    "G/Gojira/Magma",
    "S/SikTh/The Future in Whose Eyes"
  ).map(Paths.get(_))

  val tagsService = new JaudioTaggerTagsService()
  val clock = Clock.systemDefaultZone()

  directories.foreach { directory =>
    val sourceDir = Paths.get("/mnt/music/flac").resolve(directory)
    val targetDir = Paths.get("/home/alex/m4a").resolve(directory)
    val sourceStream = Files.list(sourceDir)
    sourceStream.toScala[Seq].filter(p => p.hasExtension(FLAC)).foreach { flacFile =>
      val targetFile = targetDir.resolve(sourceDir.relativize(flacFile)).withExtension(M4A)
      println(s"$flacFile -> $targetFile")
      Files.createDirectories(targetFile.getParent)
      val tags = tagsService.readTags(flacFile)
      val start = clock.instant()
      encode(flacFile, targetFile)
      tagsService.write(targetFile, tags)
      val finish = clock.instant()
      val timeTaken = Duration.between(start, finish)
      println(timeTaken)
    }
    sourceStream.close()
  }

  private def encode(flacFile: Path, targetFile: Path) = {
    Seq("flac", "-dcs", flacFile.toString) #| Seq("fdkaac", "-S", "-G", "2", "-b", "320", "-", "-o", targetFile.toString) !
  }
}
