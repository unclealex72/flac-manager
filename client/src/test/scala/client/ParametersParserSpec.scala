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

package client

import java.nio.file.{FileSystem, Files, Path}
import java.util.UUID

import cats.data.NonEmptyList
import client.ParametersParserSpec._
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import io.circe.Json
import org.specs2.mutable.Specification

/**
  * Created by alex on 21/04/17
  **/
class ParametersParserSpec extends Specification {

  sequential

  val datumFilename: String = random()
  implicit val fs: FileSystem = mkfs(
    datumFilename = datumFilename,
    stagingPaths = Seq("N" / "Napalm Death" / "Scum" / "01 You Suffer.mp3"),
    flacPaths = Seq("Q" / "Queen" / "A Night at the Opera" / "01 Death on Two Legs.mp3"))

  "Checking out files" should {
    "allow directories in the flac directory to be checked out" in {
      parse(datumFilename, "checkout", "music/flac/Q", "--unown") must beRight(
        Json.obj(
          "command" -> Json.fromString("checkout"),
          "relativeFlacDirectories" -> Json.arr(Json.fromString("Q")),
          "unown" -> Json.fromBoolean(true)
        )
      )
    }
    "not allow directories in the staging directory to be checked out" in {
      parse(datumFilename, "checkout", "music/staging/N") must failWith("/data/music/staging/N is not a flac directory.")
    }
    "not allow files in the flac directory to be checked out" in {
      parse(datumFilename, "checkout", "music/staging/Q/Queen/A Night at the Opera/01 Death on Two Legs.mp3") must failWith(
        "/data/music/staging/Q/Queen/A Night at the Opera/01 Death on Two Legs.mp3 is not a directory."
      )
    }
  }

  "Checking in files" should {
    "allow directories in the staging directory to be checked in" in {
      parse(datumFilename, "checkin", "music/staging/N") must beRight(
        Json.obj(
          "command" -> Json.fromString("checkin"),
          "relativeStagingDirectories" -> Json.arr(Json.fromString("N"))
        )
      )
    }
    "not allow directories in the flac directory to be checked in" in {
      parse(datumFilename, "checkin", "music/flac/Q") must failWith("/data/music/flac/Q is not a staging directory.")
    }
    "not allow files in the staging directory to be checked out" in {
      parse(datumFilename, "checkin", "music/staging/N/Napalm Death/Scum/01 You Suffer.mp3") must failWith(
        "/data/music/staging/N/Napalm Death/Scum/01 You Suffer.mp3 is not a directory."
      )
    }
    "not allow directories not under the /music directory to be checked out" in {
      parse(datumFilename, "checkin", "/video") must failWith(
        "/video is not relative to a datum file."
      )
    }
  }

  "Owning files" should {
    "allow directories in the staging directory to be owned" in {
      parse(datumFilename, "own", "--users", "alex,trevor", "music/staging/N") must beRight(
        Json.obj(
          "command" -> Json.fromString("own"),
          "relativeStagingDirectories" -> Json.arr(Json.fromString("N")),
          "users" -> Json.arr(Json.fromString("alex"), Json.fromString("trevor"))
        )
      )
    }
    "not allow directories in the flac directory to be owned" in {
      parse(datumFilename, "own", "--users", "alex,trevor", "music/flac/Q") must failWith("" +
        "/data/music/flac/Q is not a staging directory.")
    }
    "not allow files in the staging directory to be owned" in {
      parse(
        datumFilename,
        "own",
        "--users",
        "alex,trevor",
        "music/staging/N/Napalm Death/Scum/01 You Suffer.mp3") must failWith(
        "/data/music/staging/N/Napalm Death/Scum/01 You Suffer.mp3 is not a directory."
      )
    }
  }

  private def failWith(message: String) = {
    beLeft(NonEmptyList.of(message))
  }

  def parse(datumFilename: String, args: String*)(implicit fs: FileSystem) = ParametersParser(datumFilename, args)
  def random(): String = s".${UUID.randomUUID()}"

  def mkfs(datumFilename: String = "", stagingPaths: Seq[PathBuilder] = Seq.empty, flacPaths: Seq[PathBuilder] = Seq.empty): FileSystem = {
    val fs = MemoryFileSystemBuilder.
      newEmpty().
      setCurrentWorkingDirectory("/data").
      build(datumFilename)
    val dataPath = fs.getPath("/data")
    val musicPath = dataPath.resolve("music")
    val videoPath = dataPath.resolve("/video")
    val datumPath = musicPath.resolve(datumFilename)
    val flacPath = musicPath.resolve("flac")
    val stagingPath = musicPath.resolve("staging")
    Seq(flacPath, stagingPath, videoPath).foreach(Files.createDirectories(_))
    val allFiles = Seq(datumPath) ++ stagingPaths.map(_.toPath(stagingPath)) ++ flacPaths.map(_.toPath(flacPath))
    allFiles.foreach { file =>
      Files.createDirectories(file.getParent)
      Files.createFile(file)
    }
    fs
  }

}

object ParametersParserSpec {

  case class PathBuilder(segments: Seq[String]) {
    def /(s: String) = PathBuilder(segments :+ s)
    def toPath(parent: Path): Path = segments.foldLeft(parent) { (path, segment) =>
      path.resolve(segment)
    }
  }

  implicit class StringImplicits(path: String) {
    def /(s: String): PathBuilder = PathBuilder(Seq(path, s))
  }
}