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

package multidisc

import javax.inject.Inject

import cats.data._
import cats.implicits._
import common.files.{DirectoryService, StagedFlacFileLocation}
import common.message.MessageService
import common.music.{Tags, TagsService}

/**
  * Created by alex on 22/05/17
  **/
class MultidiscServiceImpl @Inject() (val directoryService: DirectoryService)
                                     (implicit val tagsService: TagsService) {



  case class TagsAndFileLocation(tags: Tags, fileLocation: StagedFlacFileLocation)
  object TagsAndFileLocation {
    def apply(fileLocation: StagedFlacFileLocation): ValidatedNel[String, TagsAndFileLocation] = {
      fileLocation.readTags.map(tags => TagsAndFileLocation(tags, fileLocation))
    }
  }

  def read(directories: Seq[StagedFlacFileLocation])
          (implicit messageService: MessageService): ValidatedNel[String, Seq[TagsAndFileLocation]] = {
    val empty: ValidatedNel[String, Seq[TagsAndFileLocation]] = Validated.valid(Seq.empty)
    directoryService.listFiles(directories).foldLeft(empty) { (acc, fl) =>
      val tagsAndFileLocationValidation = TagsAndFileLocation(fl)
      (acc |@| tagsAndFileLocationValidation).map(_ :+ _)
    }
  }

  def groupByAlbumId(tagsAndFileLocations: Seq[TagsAndFileLocation]): Map[String, Seq[TagsAndFileLocation]] = {
    tagsAndFileLocations.groupBy(tfl => TagSuffix.removeFrom(tfl.tags.albumId))
  }

  class Suffix(suffix: String) {

    def endsWith(str: String): Boolean = str.endsWith(suffix)

    def addTo(str: String): String = {
      if (endsWith(str)) {
        str
      }
      else {
        str + suffix
      }
    }

    def removeFrom(str: String): String = {
      if (endsWith(str)) {
        str.substring(0, str.length - suffix.length)
      }
      else {
        str
      }
    }

  }

  object TagSuffix extends Suffix("_EXTRAS")
  object NameSuffix extends Suffix(" (Extras)")
}
