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

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, _}
import cats.implicits._
import common.files.{DirectoryService, FlacFileChecker, StagedFlacFileLocation}
import common.message.Messages.{INVALID_FLAC, JOIN_ALBUM, SPLIT_ALBUM}
import common.message.{Message, MessageService, Messaging}
import common.music.{Tags, TagsService}

/**
  * Created by alex on 22/05/17
  **/
class MultiDiscServiceImpl @Inject()(val directoryService: DirectoryService)
                                    (implicit val tagsService: TagsService,
                                     val flacFileChecker: FlacFileChecker) extends MultiDiscService with Messaging {


  override def createSingleAlbum(stagedFlacFileLocations: Seq[StagedFlacFileLocation])
                          (implicit messageService: MessageService): Unit = {
    mutateAlbums(stagedFlacFileLocations, JOIN_ALBUM) { multiDiscAlbum =>
      val firstDisc = multiDiscAlbum.firstDisc
      val baseTrackNumber = firstDisc.size + 1
      val otherDiscs = multiDiscAlbum.otherDiscs.zipWithIndex.map {
        case (tfl, idx) =>
          val newTags = tfl.tags.copy(trackNumber = idx + baseTrackNumber)
          tfl.copy(tags = newTags)
      }
      firstDisc ++ otherDiscs
    }
  }

  override def createAlbumWithExtras(stagedFlacFileLocations: Seq[StagedFlacFileLocation])
                           (implicit messageService: MessageService): Unit = {
    mutateAlbums(stagedFlacFileLocations, SPLIT_ALBUM) { multiDiscAlbum =>
      val firstDisc = multiDiscAlbum.firstDisc
      val otherDiscs = multiDiscAlbum.otherDiscs.zipWithIndex.map {
        case (tfl, idx) =>
          val tags = tfl.tags
          val newTags = tags.copy(trackNumber = idx + 1, album = tags.album + " (Extras)", albumId = tags.albumId + "_EXTRAS")
          tfl.copy(tags = newTags)
      }
      firstDisc ++ otherDiscs
    }

  }

  def mutateAlbums(
                    stagedFlacFileLocations: Seq[StagedFlacFileLocation], logMessage: String => Message)
                  (mutator: MultiDiscAlbum => Seq[TagsAndFileLocation])
                  (implicit messageService: MessageService): Unit = {
    read(stagedFlacFileLocations) match {
      case Valid(tagsAndFileLocations) =>
        val multiDiscAlbums = findMultiDiscAlbums(tagsAndFileLocations)
        multiDiscAlbums.foreach { multiDiscAlbum =>
          multiDiscAlbum.firstDisc.map(_.tags).headOption.foreach { tags =>
            log(logMessage(tags.album))
          }
          val newTagsByFileLocation = fixTrackAndDiscNumbers(mutator(multiDiscAlbum)).sorted(tagsAndFileLocationOrdering)
          newTagsByFileLocation.foreach {
            case TagsAndFileLocation(newTags, fileLocation) =>
              if (multiDiscAlbum.tagsHaveChanged(fileLocation, newTags)) {
                fileLocation.writeTags(newTags)
              }
          }
        }
      case Invalid(messages) =>
        messages.toList.foreach(message => log(message))
    }

  }

  def fixTrackAndDiscNumbers(tagsAndFileLocations: Seq[TagsAndFileLocation]): Seq[TagsAndFileLocation] = {
    val empty: Seq[TagsAndFileLocation] = Seq.empty
    tagsAndFileLocations.groupBy(_.tags.albumId).values.foldLeft(empty) { (acc, tagsAndFileLocationsForDisc) =>
      val totalTracks = tagsAndFileLocationsForDisc.size
      val newTagsAndFileLocationsForDisc = tagsAndFileLocationsForDisc.sorted(tagsAndFileLocationOrdering).zipWithIndex.map {
        case(tagsAndFileLocation, idx) =>
          val newTags = tagsAndFileLocation.tags.copy(
            trackNumber = idx + 1, totalTracks = totalTracks, discNumber = 1, totalDiscs = 1)
          tagsAndFileLocation.copy(tags = newTags)
      }
      acc ++ newTagsAndFileLocationsForDisc
    }
  }

  case class MultiDiscAlbum(firstDisc: IndexedSeq[TagsAndFileLocation], otherDiscs: IndexedSeq[TagsAndFileLocation]) {
    private val tagsByFileLocation = (firstDisc ++ otherDiscs).groupBy(_.fileLocation).mapValues(_.map(_.tags).headOption)

    def tagsHaveChanged(stagedFlacFileLocation: StagedFlacFileLocation, newTags: Tags): Boolean = {
      !tagsByFileLocation.get(stagedFlacFileLocation).flatten.contains(newTags)
    }
  }


  case class TagsAndFileLocation(tags: Tags, fileLocation: StagedFlacFileLocation)
  object TagsAndFileLocation {
    def apply(fileLocation: StagedFlacFileLocation): ValidatedNel[Message, TagsAndFileLocation] = {
      fileLocation.readTags.map(tags => TagsAndFileLocation(tags, fileLocation)).leftMap(_ => NonEmptyList.of(INVALID_FLAC(fileLocation)))
    }
  }

  def read(directories: Seq[StagedFlacFileLocation])
          (implicit messageService: MessageService): ValidatedNel[Message, Seq[TagsAndFileLocation]] = {
    val empty: ValidatedNel[Message, Seq[TagsAndFileLocation]] = Validated.valid(Seq.empty)
    directoryService.listFiles(directories).filter(_.isFlacFile).foldLeft(empty) { (acc, fl) =>
      val tagsAndFileLocationValidation = TagsAndFileLocation(fl)
      (acc |@| tagsAndFileLocationValidation).map(_ :+ _)
    }
  }

  def findMultiDiscAlbums(tagsAndFileLocations: Seq[TagsAndFileLocation]): Seq[MultiDiscAlbum] = {
    val allAlbumsById = tagsAndFileLocations.groupBy(tfl => tfl.tags.albumId)
    val multiDiscAlbumsById = allAlbumsById.filter {
      case (_, tracks) => tracks.exists(track => track.tags.discNumber > 1)
    }
    multiDiscAlbumsById.values.map(toMultiDiscAlbum).toSeq
  }

  def toMultiDiscAlbum(tagsAndFileLocations: Seq[TagsAndFileLocation]): MultiDiscAlbum = {
    val (firstDisc, nextDiscs) = tagsAndFileLocations.partition(tfl => tfl.tags.discNumber == 1)
    def index(tracks: Seq[TagsAndFileLocation]): IndexedSeq[TagsAndFileLocation] =
      tracks.sorted(tagsAndFileLocationOrdering).toIndexedSeq
    MultiDiscAlbum(index(firstDisc), index(nextDiscs))
  }

  /**
    * Order tags by disc number followed by track number.
    */
  val tagsOrdering: Ordering[Tags] = Ordering.by(tags => (tags.discNumber, tags.trackNumber))

  val tagsAndFileLocationOrdering: Ordering[TagsAndFileLocation] = tagsOrdering.on(_.tags)
}
