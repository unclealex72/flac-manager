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

package multidisc

import cats.data.{Validated, _}
import cats.implicits._
import common.files.Directory.StagingDirectory
import common.files.StagingFile
import common.message.Messages.{JOIN_ALBUM, SPLIT_ALBUM}
import common.message.{Message, MessageService, Messaging}
import common.music.Tags
import javax.inject.Inject

/**
  * Created by alex on 22/05/17
  **/
class MultiDiscServiceImpl @Inject() extends MultiDiscService with Messaging {


  override def createSingleAlbum(stagingDirectories: Seq[StagingDirectory])
                          (implicit messageService: MessageService): ValidatedNel[Message, Unit] = {
    mutateAlbums(stagingDirectories, JOIN_ALBUM) { multiDiscAlbum =>
      val firstDisc: IndexedSeq[StagingFileAndTags] = multiDiscAlbum.firstDisc
      val baseTrackNumber: Int = firstDisc.size + 1
      val otherDiscs: IndexedSeq[StagingFileAndTags] = multiDiscAlbum.otherDiscs.zipWithIndex.map {
        case (tfl, idx) =>
          val newTags: Tags = tfl.tags.copy(trackNumber = idx + baseTrackNumber)
          tfl.copy(tags = newTags)
      }
      firstDisc ++ otherDiscs
    }
  }

  override def createAlbumWithExtras(stagingDirectories: Seq[StagingDirectory])
                           (implicit messageService: MessageService): ValidatedNel[Message, Unit] = {
    mutateAlbums(stagingDirectories, SPLIT_ALBUM) { multiDiscAlbum =>
      val firstDisc: IndexedSeq[StagingFileAndTags] = multiDiscAlbum.firstDisc
      val otherDiscs: IndexedSeq[StagingFileAndTags] = multiDiscAlbum.otherDiscs.zipWithIndex.map {
        case (tfl, idx) =>
          val tags: Tags = tfl.tags
          val newTags: Tags = tags.copy(trackNumber = idx + 1, album = tags.album + " (Extras)", albumId = tags.albumId + "_EXTRAS")
          tfl.copy(tags = newTags)
      }
      firstDisc ++ otherDiscs
    }

  }

  def mutateAlbums(
                    stagingDirectories: Seq[StagingDirectory], logMessage: String => Message)
                  (mutator: MultiDiscAlbum => Seq[StagingFileAndTags])
                  (implicit messageService: MessageService): ValidatedNel[Message, Unit] = {
    read(stagingDirectories).map { stagingFilesAndTags =>
      val multiDiscAlbums: Seq[MultiDiscAlbum] = findMultiDiscAlbums(stagingFilesAndTags)
      multiDiscAlbums.foreach { multiDiscAlbum =>
        multiDiscAlbum.firstDisc.map(_.tags).headOption.foreach { tags =>
          log(logMessage(tags.album))
        }
        val newTagsByFileLocation: Seq[StagingFileAndTags] = fixTrackAndDiscNumbers(mutator(multiDiscAlbum)).sorted(stagingFileAndTagsOrdering)
        newTagsByFileLocation.foreach {
          case StagingFileAndTags(stagingFile, newTags) =>
            stagingFile.writeTags(newTags)
        }
      }
    }
  }

  def fixTrackAndDiscNumbers(stagingFilesAndTags: Seq[StagingFileAndTags]): Seq[StagingFileAndTags] = {
    val empty: Seq[StagingFileAndTags] = Seq.empty
    stagingFilesAndTags.groupBy(_.tags.albumId).values.foldLeft(empty) { (acc, stagingFilesAndTagsForDisc) =>
      val totalTracks: Int = stagingFilesAndTagsForDisc.size
      val newStagingFilesAndTagsForDisc: Seq[StagingFileAndTags] = stagingFilesAndTagsForDisc.sorted(stagingFileAndTagsOrdering).zipWithIndex.map {
        case(tagsAndFileLocation, idx) =>
          val newTags: Tags = tagsAndFileLocation.tags.copy(
            trackNumber = idx + 1, totalTracks = totalTracks, discNumber = 1, totalDiscs = 1)
          tagsAndFileLocation.copy(tags = newTags)
      }
      acc ++ newStagingFilesAndTagsForDisc
    }
  }

  case class MultiDiscAlbum(firstDisc: IndexedSeq[StagingFileAndTags], otherDiscs: IndexedSeq[StagingFileAndTags])

  case class StagingFileAndTags(stagingFile: StagingFile, tags: Tags)
  object StagingFileAndTags {
    def apply(stagingFile: StagingFile)(implicit messageService: MessageService): ValidatedNel[Message, StagingFileAndTags] = {
      stagingFile.tags.read().map(tags => StagingFileAndTags(stagingFile, tags))
    }
  }

  def read(directories: Seq[StagingDirectory])
          (implicit messageService: MessageService): ValidatedNel[Message, Seq[StagingFileAndTags]] = {
    val empty: ValidatedNel[Message, Seq[StagingFileAndTags]] = Validated.valid(Seq.empty)
    directories.flatMap(_.list).filter(_.isFlacFile).foldLeft(empty) { (acc, fl) =>
      val tagsAndFileLocationValidation = StagingFileAndTags(fl)
      (acc |@| tagsAndFileLocationValidation).map(_ :+ _)
    }
  }

  def findMultiDiscAlbums(stagingFilesAndTags: Seq[StagingFileAndTags]): Seq[MultiDiscAlbum] = {
    val allAlbumsById: Map[String, Seq[StagingFileAndTags]] = stagingFilesAndTags.groupBy(tfl => tfl.tags.albumId)
    val multiDiscAlbumsById: Map[String, Seq[StagingFileAndTags]] = allAlbumsById.filter {
      case (_, tracks) => tracks.exists(track => track.tags.discNumber > 1)
    }
    multiDiscAlbumsById.values.map(toMultiDiscAlbum).toSeq
  }

  def toMultiDiscAlbum(stagingFilesAndTags: Seq[StagingFileAndTags]): MultiDiscAlbum = {
    val maybeLowestDiscNumber: Option[Int] = stagingFilesAndTags.map(_.tags.discNumber).sorted.headOption
    val (firstDisc, nextDiscs) = stagingFilesAndTags.partition(tfl => maybeLowestDiscNumber.contains(tfl.tags.discNumber))
    def index(tracks: Seq[StagingFileAndTags]): IndexedSeq[StagingFileAndTags] =
      tracks.sorted(stagingFileAndTagsOrdering).toIndexedSeq
    MultiDiscAlbum(index(firstDisc), index(nextDiscs))
  }

  /**
    * Order tags by disc number followed by track number.
    */
  val tagsOrdering: Ordering[Tags] = Ordering.by(tags => (tags.discNumber, tags.trackNumber))

  val stagingFileAndTagsOrdering: Ordering[StagingFileAndTags] = tagsOrdering.on(_.tags)
}
