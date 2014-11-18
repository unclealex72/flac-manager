package checkout

import common.configuration.{Directories, User, Users}
import common.files.{FileLocation, FileLocationExtensions, FileSystem, FlacFileLocation}
import common.message.MessageService
import common.music.{Tags, TagsService}
import common.owners.OwnerService

import scala.collection.{SortedMap, SortedSet}

/**
 * Created by alex on 17/11/14.
 */
class CheckoutServiceImpl(val fileSystem: FileSystem, val users: Users, val ownerService: OwnerService)
                         (implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions, val tagsService: TagsService)
  extends CheckoutService {

  override def checkout(flacFileLocationsByParent: SortedMap[FlacFileLocation, SortedSet[FlacFileLocation]])(implicit messageService: MessageService): Unit = {
    val tagsForUsers = flacFileLocationsByParent.foldLeft(Map.empty[User, Set[Tags]])(findTagsAndDeleteFiles)
    tagsForUsers.foreach { case (user, tagsSet) =>
      ownerService.unown(user, tagsSet)
    }
  }

  def findTagsAndDeleteFiles(tagsForUsers: Map[User, Set[Tags]], fls: (FlacFileLocation, SortedSet[FlacFileLocation]))(implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val directory = fls._1
    val flacFileLocations = fls._2
    flacFileLocations.find(_ => true) match {
      case Some(firstFlacFileLocation) => findTagsAndDeleteFiles(tagsForUsers, directory, firstFlacFileLocation, flacFileLocations)
      case None => tagsForUsers
    }
  }

  def findTagsAndDeleteFiles(
                              tagsForUsers: Map[User, Set[Tags]], directory: FlacFileLocation,
                              firstFlacFileLocation: FlacFileLocation, flacFileLocations: SortedSet[FlacFileLocation])(implicit messageService: MessageService): Map[User, Set[Tags]] = {
    val owners = quickOwners(firstFlacFileLocation)
    val tags = firstFlacFileLocation.readTags match {
      case Right(tags) => Some(tags)
      case _ => None
    }
    flacFileLocations.foreach { flacFileLocation =>
      val encodedFileLocation = flacFileLocation.toEncodedFileLocation
      val deviceFileLocations: Set[FileLocation] = owners.map { user => encodedFileLocation.toDeviceFileLocation(user)}
      (deviceFileLocations + encodedFileLocation).foreach(fileSystem.remove(_))
      fileSystem.move(flacFileLocation, flacFileLocation.toStagedFlacFileLocation)
    }
    owners.foldLeft(tagsForUsers) { (tagsForUsers, owner) =>
      tagsForUsers.get(owner) match {
        case Some(tagsSet) => tagsForUsers + (owner -> (tagsSet ++ tags))
        case _ => tagsForUsers + (owner -> tags.toSet)
      }
    }
  }


  /**
   * Find out quickly who owns an album by looking for links in the users' device repository.
   * @param flacFileLocation
   * @return
   */
  def quickOwners(flacFileLocation: FlacFileLocation): Set[User] = users.allUsers.filter { user =>
    flacFileLocation.toEncodedFileLocation.toDeviceFileLocation(user).exists
  }
}
